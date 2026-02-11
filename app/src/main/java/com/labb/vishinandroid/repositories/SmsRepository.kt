package com.labb.vishinandroid.repositories

import androidx.compose.runtime.mutableStateListOf

object SmsRepository {
    val smsList = mutableStateListOf<SmsData>()
}

data class SmsData(
    val sender: String,
    val message: String,
    val timestamp: String
)