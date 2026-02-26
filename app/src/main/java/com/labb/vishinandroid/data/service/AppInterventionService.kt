package com.labb.vishinandroid.data.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.labb.vishinandroid.data.util.SensitiveApps
import com.labb.vishinandroid.data.util.ServiceReEnableHelper
import com.labb.vishinandroid.domain.repositories.CallStateRepository
import com.labb.vishinandroid.ui.overlay.InterventionOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppInterventionService : AccessibilityService() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val warnedPackages = mutableSetOf<String>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        SensitiveApps.isSensitiveAppInForeground.set(false)
        ServiceReEnableHelper.cancelNotification(this)

        serviceScope.launch {
            CallStateRepository.isCallUnknown.collectLatest { isUnknownCall ->
                if(!isUnknownCall) {
                    warnedPackages.clear()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event == null) return

        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            val packageName = event.packageName?.toString() ?: return
            Log.d("VishingGuard_Package","Öppnad app: ${packageName}")

            if (packageName == "com.bankid.bus") {
                Log.d("VishingGuard_Package", "BankID detekterat – inaktiverar tjänster via disableSelf()")

                SensitiveApps.isSensitiveAppInForeground.set(true)

                InterventionOverlay.hide(this)

                ServiceReEnableHelper.showReEnableNotification(this)

                disableSelf()
                return
            }

            if(SensitiveApps.isSensitiveApp(packageName)){
                if(CallStateRepository.isCallUnknown.value){
                    if(!warnedPackages.contains(packageName)) {
                        InterventionOverlay.show(this)
                        warnedPackages.add(packageName)
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
    }
}