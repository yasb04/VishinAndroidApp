package com.labb.vishinandroid.data.service

import com.labb.vishinandroid.data.model.FraudRequest

interface FraudDetectionService {
    suspend fun checkFraud(request: FraudRequest): String
}
