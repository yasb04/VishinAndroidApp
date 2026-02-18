package com.labb.vishinandroid.data

import android.content.Context
import android.util.Log
import com.labb.vishinandroid.data.model.* // Importerar DecisionTree, RandomForest, etc.
import com.labb.vishinandroid.interfaces.AnalysisResult
import com.labb.vishinandroid.interfaces.FraudDetectorI
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object SwedishFraudLocalModel {

    private const val VECTOR_SIZE = 1000
    private var vocab: Map<String, Int> = emptyMap()

    private fun initialize(context: Context) {
        if (vocab.isEmpty()) {
            try {
                context.assets.open("vocab.json").use { input ->
                    val reader = BufferedReader(InputStreamReader(input))
                    val jsonString = reader.readText()
                    val jsonObject = JSONObject(jsonString)
                    val map = mutableMapOf<String, Int>()

                    jsonObject.keys().forEach { key ->
                        map[key] = jsonObject.getInt(key)
                    }
                    vocab = map
                    Log.d("FraudModel", "Vocab laddad: ${vocab.size} ord")
                }
            } catch (e: Exception) {
                Log.e("FraudModel", "Kunde inte ladda vocab.json", e)
            }
        }
    }

    /**
     * Omvandlar SMS-text till en DoubleArray som modellerna förstår.
     */
    private fun vectorize(text: String): DoubleArray {
        val vector = DoubleArray(VECTOR_SIZE) { 0.0 }
        // Dela upp texten i ord och rensa specialtecken
        val words = text.lowercase().split(Regex("[^a-zåäö0-9]+")).filter { it.isNotBlank() }

        for (word in words) {
            vocab[word]?.let { index ->
                if (index < VECTOR_SIZE) {
                    vector[index] += 1.0 // Enkel räkning (Term Frequency)
                }
            }
        }
        return vector
    }

    data class PredictResult(
        val isFraud: Boolean,
        val confidence: Float,
        val votes: Int,
        val rawScores: FloatArray,
        val tokenCount: Int
    )

    /**
     * Kör Master Ensemble-röstning.
     */
    suspend fun predict(context: Context, text: String): PredictResult {
        initialize(context)
        val inputVector = vectorize(text)

        // 1. Samla in röster från alla 5 modeller
        // För modeller som returnerar double[] (prob): [0]=Legit, [1]=Fraud
        // För modeller som returnerar double (score): > 0 är oftast Fraud

        val v1 = if (DecisionTree.score(inputVector)[1] > 0.5) 1 else 0
        val v2 = if (RandomForest.score(inputVector)[1] > 0.5) 1 else 0
        val v3 = if (LogisticRegression.score(inputVector) > 0.0) 1 else 0
        val v4 = if (LogisticRegressionModel.score(inputVector) > 0.0) 1 else 0
        val v5 = if (SVM_Fast.score(inputVector) > 0.0) 1 else 0

        val totalVotes = v1 + v2 + v3 + v4 + v5

        // Majoritetsbeslut (3 av 5 räcker för varning)
        val isFraud = totalVotes >= 3

        // Räkna ut "confidence" baserat på hur eniga modellerna var
        val confidence = totalVotes.toFloat() / 5.0f

        Log.d("FraudModel", "Resultat: $isFraud (Röster: $totalVotes/5)")

        return PredictResult(
            isFraud = isFraud,
            confidence = confidence,
            rawScores = floatArrayOf(totalVotes.toFloat()),
            votes = totalVotes,
            tokenCount = text.split(" ").size
        )
    }

    fun close() {
        // Ingen TFLite-interpreter behöver stängas längre!
    }
}