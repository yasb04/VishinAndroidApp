package com.labb.vishinandroid.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.labb.vishinandroid.ui.RecordingOverlay

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("VishingGuard", "CallReceiver VAKNADE! Action: ${intent.action}")
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("VishingGuard", "Samtal besvarat - visar fråga")
                RecordingOverlay.show(context)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d("VishingGuard", "Samtal avslutat - stänger allt")
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