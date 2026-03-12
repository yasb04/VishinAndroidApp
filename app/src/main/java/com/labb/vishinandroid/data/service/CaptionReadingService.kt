package com.labb.vishinandroid.data.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.labb.vishinandroid.data.factories.FraudDetectorFactory
import com.labb.vishinandroid.data.util.CaptionUtils.extractNewText
import com.labb.vishinandroid.domain.repositories.CallRepository
import com.labb.vishinandroid.domain.interfaces.FraudDetector
import com.labb.vishinandroid.data.util.SensitiveApps
import com.labb.vishinandroid.data.util.ServiceReEnableHelper
import com.labb.vishinandroid.ui.overlay.OverlayHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CaptionReadingService : AccessibilityService() {
    private val TAG = "VishingGuard_DEBUG"
    private val TARGET_CAPTION_ID = "com.google.android.as:id/captions_text"
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val modelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var fraudDetector: FraudDetector

    // Buffer settings
    private val BUFFER_FLUSH_INTERVAL_MS = 2000L
    private val BUFFER_SIZE_THRESHOLD = 100

    private val textBuffer = StringBuilder()
    private var lastFullCaptionText = ""

    private var modelJob: Job? = null
    private var flushScheduledJob: Job? = null
    private val modelMutex = Mutex()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (SensitiveApps.isSensitiveAppInForeground.get()) {
            Log.d(TAG, "BankID-läge aktivt – inaktiverar CaptionReadingService via disableSelf()")
            ServiceReEnableHelper.showReEnableNotification(this)
            disableSelf()
            return
        }

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

        if (viewId == TARGET_CAPTION_ID && !text.isNullOrBlank()) {
            if (text != "…" && text != "...") {
                processIncrementalText(text)
            }
            return
        }

        for (i in 0 until node.childCount) {
            findCaptionText(node.getChild(i))
        }
    }

    private fun processIncrementalText(newFullText: String) {
        val cleaned = newFullText
            .replace(Regex("\\[.*?\\]"), "")
            .replace("...", "")
            .replace("…", "")
            .trim()
        if (cleaned == lastFullCaptionText || cleaned.isEmpty()) return

        val textToAppend = extractNewText(lastFullCaptionText, cleaned)

        lastFullCaptionText = cleaned

        if (textToAppend.isEmpty()) return



        Log.d(TAG, "NEW SPEECH CAPTURED: $textToAppend")
        synchronized(textBuffer) {
            if (textBuffer.isNotEmpty()) textBuffer.append(' ')
            textBuffer.append(textToAppend)
        }
        if (textBuffer.length >= BUFFER_SIZE_THRESHOLD) {
            flushBufferToModel()
        } else {
            scheduleFlush()
        }
    }

    private fun scheduleFlush() {
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
        lastFullCaptionText = ""

        modelJob?.cancel()
        modelJob = modelScope.launch {
            modelMutex.withLock {
                try {
                    Log.d(TAG, "AI Analyzing: $toAnalyze")
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
        SensitiveApps.isSensitiveAppInForeground.set(false)
        ServiceReEnableHelper.cancelNotification(this)

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
