package com.labb.vishinandroid.data.service

import com.labb.vishinandroid.repositories.SmsData
import com.labb.vishinandroid.repositories.SmsRepository


import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.sql.Time
import java.sql.Timestamp

class NotificationReceiver : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val extras = notification.extras

        val packageName = sbn.packageName

        if (packageName == "com.google.android.apps.messaging" ||
            packageName == "com.samsung.android.messaging") {


            val title = extras.getString("android.title") ?: "Okänd"
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            Log.d("VishingGuard", "Notis fångad från $packageName: $title - $text")

            if (text.isNotEmpty()) {
                // Spara till din lista (Samma som förut!)
                // Vi fejkar tiden här för enkelhetens skull
                SmsRepository.smsList.add(0, SmsData(title, text, "nu"))
            }
        }
    }
}