package com.labb.vishinandroid.domain.model

data class FraudRequest(
    val message: String,
    val phoneNumber: String?,
    val email: String?
)
