package com.labb.vishinandroid.domain.model

data class AnalysisResult(
    val isFraud: Boolean,
    val score: Float,
)