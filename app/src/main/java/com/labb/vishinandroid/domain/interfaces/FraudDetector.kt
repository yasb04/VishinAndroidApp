package com.labb.vishinandroid.domain.interfaces

import com.labb.vishinandroid.domain.model.AnalysisResult

interface FraudDetector {
    suspend fun analyze(text: String): AnalysisResult
}