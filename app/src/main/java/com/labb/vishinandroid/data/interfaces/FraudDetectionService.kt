package com.labb.vishinandroid.data.interfaces

import com.labb.vishinandroid.domain.model.FraudRequest

interface FraudDetectionService {
    suspend fun checkFraud(request: FraudRequest): String
}