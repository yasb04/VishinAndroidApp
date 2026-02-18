package com.labb.vishinandroid.ml



import com.labb.vishinandroid.interfaces.AnalysisResult
import com.labb.vishinandroid.interfaces.FraudDetectorI

class SimpleRuleBasedDetector : FraudDetectorI {
    override suspend fun analyze(text: String): AnalysisResult {
        val lowerText = text.lowercase()


        if (lowerText.contains("kala") ||
          //  lowerText.contains("swish") ||
            lowerText.contains("polisen") ||
            lowerText.contains("ksort") ||
            lowerText.contains("scam"))
                {

            return AnalysisResult(
                isFraud = true,
                score = 0.95f
            )
        }

        return AnalysisResult(isFraud = false, score = 0.1f)
    }
}