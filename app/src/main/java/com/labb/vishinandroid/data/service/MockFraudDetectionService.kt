package com.labb.vishinandroid.data.service

import com.labb.vishinandroid.data.model.FraudRequest
import kotlinx.coroutines.delay

class MockFraudDetectionService : FraudDetectionService {
    override suspend fun checkFraud(request: FraudRequest): String {

        val isSuspicious = request.message.contains("fraud", ignoreCase = true) || 
                           request.message.contains("scam", ignoreCase = true) ||
                           request.message.contains("free money", ignoreCase = true)

        if (isSuspicious) {
            return "Warning: High risk of fraud detected!"
        }
        
        if (request.phoneNumber != null) {
            return "appears safe."
        }
        
        if (request.email != null) {
            return "appears safe."
        }

        return "Message appears safe."
    }
}
