package com.labb.vishinandroid.data.service

import com.labb.vishinandroid.repositories.SmsData
import com.labb.vishinandroid.repositories.SmsRepository


import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.labb.vishinandroid.interfaces.FraudDetectorI
import com.labb.vishinandroid.ml.SimpleRuleBasedDetector
import com.labb.vishinandroid.ui.overlay.OverlayHelper
import com.labb.vishinandroid.ui.overlay.OverlayHelper.showWarningOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotificationReceiver : NotificationListenerService() {

    private lateinit var fraudDetector: FraudDetectorI

    override fun onCreate() {
        super.onCreate()
        // Välj vilken hjärna vi ska använda för att analysera notisarna:
        fraudDetector = SimpleRuleBasedDetector()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val extras = notification.extras

        val packageName = sbn.packageName

        if (packageName == "com.google.android.apps.messaging" ||
            packageName == "com.samsung.android.messaging") {


            val title = extras.getString("android.title") ?: "Okänd"
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            CoroutineScope(Dispatchers.IO).launch {

                val result = fraudDetector.analyze(text)

                if (result.isFraud) {
                    withContext(Dispatchers.Main) {
                        OverlayHelper.showWarningOverlay(
                            context = applicationContext,
                            smsText = text,
                            score = result.score
                        )
                    }
                }
            }

            Log.d("VishingGuard", "Notis fångad från $packageName: $title - $text")

            if (text.isNotEmpty()) {
                SmsRepository.smsList.add(0, SmsData(title, text, "nu"))
            }
        }
    }
}