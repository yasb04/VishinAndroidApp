package com.labb.vishinandroid.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in messages) {

                val sender = sms.originatingAddress
                val messageBody = sms.messageBody

                Toast.makeText(context, "SMS FÅNGAT: $messageBody", Toast.LENGTH_LONG).show()

                Log.d("VishingGuard_SMS", "Nytt SMS från: $sender")
                Log.d("VishingGuard_SMS", "Text: $messageBody")

            }
        }
    }
}