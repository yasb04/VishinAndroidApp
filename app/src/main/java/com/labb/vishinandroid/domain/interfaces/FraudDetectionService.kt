package com.labb.vishinandroid.domain.interfaces

import com.labb.vishinandroid.domain.model.FraudRequest

interface FraudDetectionService {
    suspend fun checkFraud(request: FraudRequest): String
}