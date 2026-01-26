package com.labb.vishinandroid.data.model

data class FraudRequest(
    val message: String,
    val phoneNumber: String?,
    val email: String?
)
