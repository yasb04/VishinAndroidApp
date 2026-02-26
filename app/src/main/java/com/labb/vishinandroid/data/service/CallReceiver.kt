package com.labb.vishinandroid.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.labb.vishinandroid.domain.repositories.CallRepository
import com.labb.vishinandroid.domain.repositories.CallStateRepository
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
                    val isKnownContact = if (!incomingNumber.isNullOrEmpty()) {
                        inPhoneBook(incomingNumber, context)
                    } else {
                        false
                    }

                    if (!isKnownContact) {
                        Log.d("VishingGuard", "Okänt nummer detekterat! Aktiverar säkerhetsläge.")
                        CallStateRepository.setCallUnknown(true)
                    } else {
                        Log.d("VishingGuard", "Känd kontakt. Inget säkerhetsläge.")
                        CallStateRepository.setCallUnknown(false)
                    }
                    RecordingOverlay.show(context)
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d(TAG, "Samtal avslutat.")
                    CallStateRepository.setCallUnknown(false)
                    RecordingOverlay.hide(context)
                }
            }
        }
    }


    fun inPhoneBook(phoneNumber: String, context: Context): Boolean {
        val question = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val answer = context.contentResolver.query(question, null, null, null, null)
        try {
            if (answer != null && answer.moveToFirst()) {
                return true
            }
        } finally {
            answer?.close()
        }
        return false
    }
}