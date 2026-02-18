package com.labb.vishinandroid.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.labb.vishinandroid.repositories.CallStateRepository
import com.labb.vishinandroid.ui.overlay.RecordingOverlay

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("VishingGuard", "CallReceiver VAKNADE! Action: ${intent.action}")
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        Log.d("VishingGuard_Incoming","Inkommande nummer: ${incomingNumber}")

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("VishingGuard", "Samtal besvarat - visar fråga")

                // Kontrollera om numret är känt
                val isKnownContact = if (!incomingNumber.isNullOrEmpty()) {
                    inPhoneBook(incomingNumber, context)
                } else {
                    // Om numret är dolt eller null, betrakta det som okänt för säkerhets skull
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
                Log.d("VishingGuard", "Samtal avslutat - stänger allt")
                CallStateRepository.setCallUnknown(false)
                RecordingOverlay.hide(context)


                val serviceIntent = Intent(context, RecordingService::class.java)
                context.stopService(serviceIntent)
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