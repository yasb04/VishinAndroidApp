package com.labb.vishinandroid.data.factories

import android.content.Context
import com.labb.vishinandroid.data.fraudDetectors.EnsembleDetector
import com.labb.vishinandroid.domain.interfaces.FraudDetector
import com.labb.vishinandroid.domain.repositories.SupabaseFraudEventRepository

object FraudDetectorFactory {
    fun getDetector(context: Context): FraudDetector {

        return EnsembleDetector(
            context = context,
            fraudEventRepository = SupabaseFraudEventRepository()
        )

    }
}