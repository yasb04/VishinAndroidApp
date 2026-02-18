package com.labb.vishinandroid.domain.repositories

import androidx.compose.runtime.mutableStateListOf

object SmsRepository {
    val smsList = mutableStateListOf<SmsData>()
}

data class SmsData(
    val sender: String,
    val message: String,
    val timestamp: String
)