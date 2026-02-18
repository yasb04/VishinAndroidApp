package com.labb.vishinandroid.data.factories

import android.content.Context
import com.labb.vishinandroid.data.fraudDetectors.SwedishFraudLocalModel
import com.labb.vishinandroid.data.interfaces.FraudDetector

object FraudDetectorFactory {
    fun getDetector(context: Context): FraudDetector {

        return SwedishFraudLocalModel(context)

    }
}