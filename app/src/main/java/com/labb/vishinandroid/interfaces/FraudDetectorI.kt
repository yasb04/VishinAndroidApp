package com.labb.vishinandroid.interfaces



data class AnalysisResult(
    val isFraud: Boolean,
    val score: Float,
    //val reason: String
)

interface FraudDetectorI {
    suspend fun analyze(text: String): AnalysisResult
}