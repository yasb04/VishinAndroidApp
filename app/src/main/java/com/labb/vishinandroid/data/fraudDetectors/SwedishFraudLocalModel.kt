package com.labb.vishinandroid.data.fraudDetectors

import android.content.Context
import android.util.Log

import com.labb.vishinandroid.data.interfaces.FraudDetector
import com.labb.vishinandroid.data.util.AnalysisResult
import com.labb.vishinandroid.domain.model.DecisionTree
import com.labb.vishinandroid.domain.model.LogisticRegression
import com.labb.vishinandroid.domain.model.LogisticRegressionModel
import com.labb.vishinandroid.domain.model.RandomForest
import com.labb.vishinandroid.domain.model.SVM_Fast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


class SwedishFraudLocalModel(private val context: Context) : FraudDetector {

    companion object {
        private const val VECTOR_SIZE = 1000
        private const val TAG = "SwedishFraudModel"
    }

    private val vocab: Map<String, Int> by lazy {
        loadVocab()
    }

    private fun loadVocab(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        try {
            context.assets.open("vocab.json").use { input ->
                val reader = BufferedReader(InputStreamReader(input))
                val jsonString = reader.readText()
                val jsonObject = JSONObject(jsonString)

                jsonObject.keys().forEach { key ->
                    map[key] = jsonObject.getInt(key)
                }
            }
            Log.d(TAG, "Vocab laddad: ${map.size} ord")
        } catch (e: Exception) {
            Log.e(TAG, "Kunde inte ladda vocab.json", e)
        }
        return map
    }


    override suspend fun analyze(text: String): AnalysisResult {

        val inputVector = vectorize(text)

        val v1 = if (DecisionTree.score(inputVector)[1] > 0.5) 1 else 0
        val v2 = if (RandomForest.score(inputVector)[1] > 0.5) 1 else 0
        val v3 = if (LogisticRegression.score(inputVector) > 0.0) 1 else 0
        val v4 = if (LogisticRegressionModel.score(inputVector) > 0.0) 1 else 0
        val v5 = if (SVM_Fast.score(inputVector) > 0.0) 1 else 0

        val totalVotes = v1 + v2 + v3 + v4 + v5

        val isFraud = totalVotes >= 3
        val confidence = totalVotes.toFloat() / 5.0f // T.ex. 0.8 om 4 av 5 håller med

        Log.d(TAG, "Ensemble Resultat: $isFraud (Röster: $totalVotes/5)")

        return AnalysisResult(
            isFraud = isFraud,
            score = confidence,

        )
    }


    private fun vectorize(text: String): DoubleArray {
        val currentVocab = vocab

        val vector = DoubleArray(VECTOR_SIZE) { 0.0 }
        val words = text.lowercase().split(Regex("[^a-zåäö0-9]+")).filter { it.isNotBlank() }

        for (word in words) {
            currentVocab[word]?.let { index ->
                if (index < VECTOR_SIZE) {
                    vector[index] += 1.0
                }
            }
        }
        return vector
    }
}