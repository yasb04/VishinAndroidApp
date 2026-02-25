package com.labb.vishinandroid.domain.repositories

import com.labb.vishinandroid.domain.model.LowConfidenceFraudEvent

interface FraudEventRepository {
    suspend fun saveLowConfidenceEvent(event: LowConfidenceFraudEvent)
}
