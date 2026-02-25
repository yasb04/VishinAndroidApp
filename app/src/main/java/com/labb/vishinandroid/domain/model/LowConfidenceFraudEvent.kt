package com.labb.vishinandroid.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LowConfidenceFraudEvent(
    @SerialName("text") val text: String,
    @SerialName("accuracy") val accuracy: Float,
    @SerialName("metadata") val metadata: String? = null,
    @SerialName("detector") val detector: String,
)
