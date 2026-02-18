package com.labb.vishinandroid.data.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.labb.vishinandroid.data.factories.FraudDetectorFactory
import com.labb.vishinandroid.domain.repositories.CallRepository
import com.labb.vishinandroid.data.interfaces.FraudDetector
import com.labb.vishinandroid.ui.overlay.OverlayHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CaptionReadingService : AccessibilityService() {
    private val TAG = "VishingGuard_DEBUG"

    // Target the specific ID found in your Logcat
    private val TARGET_CAPTION_ID = "com.google.android.as:id/captions_text"

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val modelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var fraudDetector: FraudDetector

    // Buffer settings
    private val BUFFER_FLUSH_INTERVAL_MS = 2000L // Wait 2s for more speech before analyzing
    private val BUFFER_SIZE_THRESHOLD = 100      // Analyze if buffer gets long

    private val textBuffer = StringBuilder()
    private var lastFullCaptionText = ""

    private var modelJob: Job? = null
    private var flushScheduledJob: Job? = null
    private val modelMutex = Mutex()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Scans all windows (required for system overlays like Live Caption)
        val currentWindows = windows
        for (window in currentWindows) {
            if (window.type == AccessibilityWindowInfo.TYPE_SYSTEM ||
                window.type == AccessibilityWindowInfo.TYPE_APPLICATION) {

                findCaptionText(window.root)
            }
        }
    }

    private fun findCaptionText(node: AccessibilityNodeInfo?) {
        if (node == null) return

        val viewId = node.viewIdResourceName ?: ""
        val text = node.text?.toString()

        // 1. STRICT FILTER: Only look for the confirmed Caption ID
        if (viewId == TARGET_CAPTION_ID && !text.isNullOrBlank()) {
            // Ignore the "..." loading state
            if (text != "…" && text != "...") {
                processIncrementalText(text)
            }
            return // Found what we need, stop searching this branch
        }

        // 2. RECURSE: Keep searching children for the target ID
        for (i in 0 until node.childCount) {
            findCaptionText(node.getChild(i))
        }
    }

    private fun processIncrementalText(newFullText: String) {
        val cleaned = newFullText.trim()

        // Skip if nothing changed
        if (cleaned == lastFullCaptionText) return

        // CALCULATE NEW WORDS:
        // Live Caption builds sentences: "My name", "My name is", "My name is Bob".
        // We only want the part that wasn't in 'lastFullCaptionText'.
        val textToAppend = if (lastFullCaptionText.isNotEmpty() && cleaned.startsWith(lastFullCaptionText)) {
            cleaned.substring(lastFullCaptionText.length).trim()
        } else {
            // If the text box cleared or changed completely, start fresh
            cleaned
        }

        if (textToAppend.isEmpty()) return

        // Update tracking state
        lastFullCaptionText = cleaned
        Log.d(TAG, "🟢 NEW SPEECH CAPTURED: $textToAppend")

        // Add only the NEW part to the buffer
        synchronized(textBuffer) {
            if (textBuffer.isNotEmpty()) textBuffer.append(' ')
            textBuffer.append(textToAppend)
        }

        // Trigger AI analysis logic
        if (textBuffer.length >= BUFFER_SIZE_THRESHOLD) {
            flushBufferToModel()
        } else {
            scheduleFlush()
        }
    }

    private fun scheduleFlush() {
        // Debounce: Cancel previous timer and start a new one
        flushScheduledJob?.cancel()
        flushScheduledJob = serviceScope.launch {
            delay(BUFFER_FLUSH_INTERVAL_MS)
            flushBufferToModel()
        }
    }

    private fun flushBufferToModel() {
        val toAnalyze: String
        synchronized(textBuffer) {
            if (textBuffer.isEmpty()) return
            toAnalyze = textBuffer.toString()
            textBuffer.clear()
        }

        // Reset tracking so we can detect new sentences properly
        lastFullCaptionText = ""

        modelJob?.cancel()
        modelJob = modelScope.launch {
            modelMutex.withLock {
                try {
                    Log.d(TAG, "🧠 AI Analyzing: $toAnalyze")
                    val result = fraudDetector.analyze(toAnalyze)

                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "AI Result: Fraud=${result.isFraud} Conf=${result.score}")
                        CallRepository.addTextToActiveSession(toAnalyze, result.isFraud)
                        if (result.isFraud) {
                            OverlayHelper.showWarningOverlay(applicationContext, toAnalyze, result.score)
                        }
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) Log.e(TAG, "Model failure", e)
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        fraudDetector = FraudDetectorFactory.getDetector(context = applicationContext)
        Log.d(TAG, "VishingGuard: Monitoring Live Caption ($TARGET_CAPTION_ID)")
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        serviceScope.cancel()
        modelScope.cancel()
        super.onDestroy()
    }
}
