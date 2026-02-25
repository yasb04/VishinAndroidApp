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
    // Lista för vilka appar vi redan varnat för, så man inte "spammar" overlayen (annars är den alltid på)
    private val warnedPackages = mutableSetOf<String>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Nollställ BankID-flaggan när tjänsten startas då användaren har aktiverat igen
        SensitiveApps.isSensitiveAppInForeground.set(false)
        ServiceReEnableHelper.cancelNotification(this)

        // Nollställer listan när samtalet avslutas
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

        // Kollar om man öppnar/byter en app
        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            val packageName = event.packageName?.toString() ?: return
            Log.d("VishingGuard_Package","Öppnad app: ${packageName}")

            // Om BankID öppnas så inaktiverar man tjänsterna helt så BankID inte blockeras
            if (packageName == "com.bankid.bus") {
                Log.d("VishingGuard_Package", "BankID detekterat – inaktiverar tjänster via disableSelf()")

                // Sätt flagga så att CaptionReadingService också inaktiveras, annars funkar inte BankID
                SensitiveApps.isSensitiveAppInForeground.set(true)

                InterventionOverlay.hide(this)

                // Notifikation för att återaktivera
                ServiceReEnableHelper.showReEnableNotification(this)

                // Inaktivera denna tjänst – tar bort den från systemets lista
                disableSelf()
                return
            }

            // Övriga känsliga appar (Swish, banker) visar intervention-overlay vid okänt samtal
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
        // Körs när tjänsten avbryts
    }
}