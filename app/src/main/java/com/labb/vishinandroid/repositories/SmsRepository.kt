package com.labb.vishinandroid.repositories

import androidx.compose.runtime.mutableStateListOf

// En global lista som vi kan nå från både Receiver och UI
object SmsRepository {
    // Vi använder en "StateList" så att UI:t uppdateras automatiskt när vi lägger till något
    val smsList = mutableStateListOf<SmsData>()
}

data class SmsData(
    val sender: String,
    val message: String,
    val timestamp: String
)