package com.labb.vishinandroid.data.service


import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.labb.vishinandroid.ml.SimpleRuleBasedDetector
import com.labb.vishinandroid.ui.overlay.OverlayHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaptionReadingService : AccessibilityService() {

    private val fraudDetector = SimpleRuleBasedDetector()

    private var lastProcessedText: String = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Hämta rotnoden för hela skärmen
        val rootNode = rootInActiveWindow ?: return

        // Skanna igenom hela skärmen efter text
        traverseNode(rootNode)
    }

    private fun traverseNode(node: AccessibilityNodeInfo?) {
        if (node == null) return

        // 1. Har noden text?
        if (node.text != null && node.text.isNotEmpty()) {
            val screenText = node.text.toString()

            // 2. Filtrera bort korta grejer (klockslag, batteri, knappar)
            // Live Caption-text brukar vara meningar, så vi kollar längd > 10
            if (screenText.length > 15 && screenText != lastProcessedText) {

                // Logga vad vi hittade (för debugging)
                Log.d("VishingGuard_Screen", "Hittade text: $screenText")
                lastProcessedText = screenText

                // 3. Skicka till AI:n!
                analyzeText(screenText)
            }
        }

        // 4. Fortsätt leta i barn-noder (rekursivt)
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            traverseNode(childNode)
            childNode?.recycle() // Viktigt för minnet!
        }
    }

    private fun analyzeText(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = fraudDetector.analyze(text)

            if (result.isFraud) {
                Log.e("VishingGuard_Screen", "BEDRÄGERI UPPTÄCKT PÅ SKÄRMEN!")
                OverlayHelper.showWarningOverlay(
                    context = applicationContext,
                    smsText = text,
                    score = result.score
                )
            }
        }
    }

    override fun onInterrupt() {
        // Körs om tjänsten avbryts
    }
}