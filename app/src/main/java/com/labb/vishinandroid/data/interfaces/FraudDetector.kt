package com.labb.vishinandroid.data.interfaces

import com.labb.vishinandroid.data.util.AnalysisResult

interface FraudDetector {
    suspend fun analyze(text: String): AnalysisResult
}