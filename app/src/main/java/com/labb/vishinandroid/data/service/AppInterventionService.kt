package com.labb.vishinandroid.data.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.labb.vishinandroid.data.util.SensitiveApps
import com.labb.vishinandroid.repositories.CallStateRepository
import com.labb.vishinandroid.ui.overlay.InterventionOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppInterventionService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    // Lista för vilka appar vi redan varnat för, så man inte "spammar" overlayen (annars är den alltid på)
   /* private val warnedPackages = mutableSetOf<String>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Nollställer listan när samtalet avslutas
        serviceScope.launch {
            CallStateRepository.isCallUnknown.collectLatest { isUnknownCall ->
                if(!isUnknownCall) {
                    warnedPackages.clear()
                }
            }
        }
    } */

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event == null) return

        // Kollar om man öppnar/byter en app
        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            val packageName = event.packageName?.toString() ?: return
            Log.d("VishingGuard_Package","Öppnad app: ${packageName}")
            // Om appens paketnamn är med i listan så visas interventionoverlay
            if(SensitiveApps.isSensitiveApp(packageName)){
                //    if(CallStateRepository.isCallUnknown.value){ ta bort kommentar efter - detta för test
                    //    if(!warnedPackages.contains(packageName)) {
                            InterventionOverlay.show(applicationContext)
                     //       warnedPackages.add(packageName)
                    //    }
                  //  } ta bort komentar efter  detta för test
            }
        }
    }

    override fun onInterrupt() {
        // Körs när tjänsten avbryts
    }
}