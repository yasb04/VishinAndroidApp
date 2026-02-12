package com.labb.vishinandroid.data

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object SwedishFraudLocalModel {

    private const val SEQUENCE_LENGTH = 64
    private const val MODEL_FILE = "swedish_fraud_model.tflite"
    private const val MAX_WORD_PIECE_LENGTH = 200

    // Vocab mapping: token -> id (line index in vocab.txt)
    private var vocab: Map<String, Int> = emptyMap()

    private fun ensureVocabLoaded(context: Context) {
        if (vocab.isNotEmpty()) return

        val map = mutableMapOf<String, Int>()
        context.assets.open("vocab.txt").use { input ->
            BufferedReader(InputStreamReader(input)).use { reader ->
                var index = 0
                reader.lineSequence().forEach { token ->
                    map[token] = index
                    index++
                }
            }
        }
        vocab = map
    }

    // ── WordPiece tokenizer ────────────────────────────────────────────

    /**
     * Split text into basic tokens: lowercase, split on whitespace,
     * and separate punctuation so it gets its own token.
     * e.g. "Hej, hur mår du?" → ["hej", ",", "hur", "mår", "du", "?"]
     */
    private fun basicTokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()

        for (ch in text.lowercase()) {
            when {
                ch.isWhitespace() -> {
                    if (current.isNotEmpty()) { result.add(current.toString()); current.clear() }
                }
                // Separate punctuation into its own token
                ch.isLetterOrDigit().not() -> {
                    if (current.isNotEmpty()) { result.add(current.toString()); current.clear() }
                    result.add(ch.toString())
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) result.add(current.toString())
        return result
    }

    /**
     * WordPiece: for each word, greedily match the longest prefix in vocab,
     * then continue with "##"-prefixed substrings.
     * Falls back to [UNK] if the word can't be segmented at all.
     */
    private fun wordPieceTokenize(word: String): List<Int> {
        if (word.length > MAX_WORD_PIECE_LENGTH) return listOf(vocab["[UNK]"] ?: 1)

        val ids = mutableListOf<Int>()
        var start = 0

        while (start < word.length) {
            var end = word.length
            var matched = false

            while (start < end) {
                val substr = if (start == 0) word.substring(start, end)
                             else "##" + word.substring(start, end)

                val id = vocab[substr]
                if (id != null) {
                    ids.add(id)
                    matched = true
                    break
                }
                end--
            }

            if (!matched) {
                // Entire word is unknown
                return listOf(vocab["[UNK]"] ?: 1)
            }
            start = end
        }
        return ids
    }

    /**
     * Full tokenization: [CLS] + WordPiece tokens + [SEP], padded to SEQUENCE_LENGTH.
     */
    private fun tokenizeToIds(text: String): IntArray {
        val ids = IntArray(SEQUENCE_LENGTH) { 0 } // 0 = [PAD]

        val clsId = vocab["[CLS]"] ?: 2
        val sepId = vocab["[SEP]"] ?: 3

        var pos = 0
        ids[pos++] = clsId

        val words = basicTokenize(text)
        for (word in words) {
            val pieces = wordPieceTokenize(word)
            for (pieceId in pieces) {
                if (pos >= SEQUENCE_LENGTH - 1) break
                ids[pos++] = pieceId
            }
            if (pos >= SEQUENCE_LENGTH - 1) break
        }

        if (pos < SEQUENCE_LENGTH) {
            ids[pos] = sepId
        }

        return ids
    }

    // ── Model loading ──────────────────────────────────────────────────

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fd = context.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fd.fileDescriptor)
        return inputStream.channel.map(
            FileChannel.MapMode.READ_ONLY,
            fd.startOffset,
            fd.declaredLength
        )
    }

    // ── Public API ─────────────────────────────────────────────────────

    data class PredictResult(
        val scores: FloatArray,
        val inputCount: Int,
        val outputShape: IntArray,
        val tokenCount: Int
    )

    suspend fun predict(context: Context, text: String): PredictResult {
        ensureVocabLoaded(context)

        val inputIds = tokenizeToIds(text)

        // Count real (non-padding) tokens for the attention mask
        val tokenCount = inputIds.count { it != 0 }

        // Attention mask: 1 for real tokens, 0 for padding
        val attentionMask = IntArray(SEQUENCE_LENGTH) { if (inputIds[it] != 0) 1 else 0 }

        // Token type IDs: all 0 for single-sentence classification
        val tokenTypeIds = IntArray(SEQUENCE_LENGTH) { 0 }

        val interpreter = Interpreter(loadModelFile(context))
        val inputCount = interpreter.inputTensorCount
        val outputCount = interpreter.outputTensorCount

        // Log model info for debugging
        for (i in 0 until inputCount) {
            val t = interpreter.getInputTensor(i)
            Log.d("FraudModel", "Input[$i]: name=${t.name()}, shape=${t.shape().contentToString()}, type=${t.dataType()}")
        }
        for (i in 0 until outputCount) {
            val t = interpreter.getOutputTensor(i)
            Log.d("FraudModel", "Output[$i]: name=${t.name()}, shape=${t.shape().contentToString()}, type=${t.dataType()}")
        }

        Log.d("FraudModel", "Token IDs (first 20): ${inputIds.take(20)}")
        Log.d("FraudModel", "Attention mask (first 20): ${attentionMask.take(20)}")

        // Prepare output
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputMap = mutableMapOf<Int, Any>()
        for (i in 0 until outputCount) {
            val shape = interpreter.getOutputTensor(i).shape()
            outputMap[i] = Array(shape[0]) { FloatArray(shape.drop(1).fold(1) { a, b -> a * b }) }
        }

        if (inputCount == 1) {
            // Model only takes input_ids
            val inputs = arrayOf<Any>(Array(1) { inputIds })
            interpreter.runForMultipleInputsOutputs(inputs, outputMap)
        } else {
            // Model takes multiple inputs (typically: input_ids, attention_mask, token_type_ids)
            val inputs = arrayOfNulls<Any>(inputCount)
            for (i in 0 until inputCount) {
                val name = interpreter.getInputTensor(i).name().lowercase()
                inputs[i] = when {
                    name.contains("input_id") || name.contains("input_word") || i == 0
                        -> Array(1) { inputIds }
                    name.contains("attention") || name.contains("mask") || i == 1
                        -> Array(1) { attentionMask }
                    name.contains("token_type") || name.contains("segment") || i == 2
                        -> Array(1) { tokenTypeIds }
                    else -> Array(1) { IntArray(SEQUENCE_LENGTH) { 0 } }
                }
            }
            interpreter.runForMultipleInputsOutputs(inputs as Array<Any>, outputMap)
        }

        val scores = (outputMap[0] as Array<FloatArray>)[0]
        Log.d("FraudModel", "Raw output scores: ${scores.contentToString()}")

        interpreter.close()

        return PredictResult(scores, inputCount, outputShape, tokenCount)
    }
}
