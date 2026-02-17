package com.labb.vishinandroid.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.labb.vishinandroid.repositories.CallRepository
import com.labb.vishinandroid.ui.overlay.RecordingOverlay

class CallReceiver : BroadcastReceiver() {
    private val TAG = "VishingGuard_DEBUG"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Dolt nummer"

            Log.d(TAG, "Telefonstatus ändrad: $state | Nummer: $incomingNumber")

            when (state) {
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d(TAG, "Samtal besvarat. Initierar loggning.")
                    CallRepository.startNewSession(incomingNumber)
                    RecordingOverlay.show(context)
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d(TAG, "Samtal avslutat.")
                    RecordingOverlay.hide(context)
                }
            }
        }
    }
}