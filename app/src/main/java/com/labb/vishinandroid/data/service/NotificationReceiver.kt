package com.labb.vishinandroid.data.service

import com.labb.vishinandroid.domain.repositories.SmsData
import com.labb.vishinandroid.domain.repositories.SmsRepository
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.labb.vishinandroid.data.factories.FraudDetectorFactory
import com.labb.vishinandroid.domain.interfaces.FraudDetector
import com.labb.vishinandroid.ui.overlay.OverlayHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotificationReceiver : NotificationListenerService() {

    private lateinit var fraudDetector: FraudDetector

    private val messageApps = setOf(
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.whatsapp",
        "com.whatsapp.w4b",
        "com.facebook.orca"
    )

    private val emailApps = setOf(
        "com.samsung.android.email.provider",
        "com.google.android.gm",
        "com.microsoft.office.outlook"
    )

    override fun onCreate() {
        super.onCreate()
        fraudDetector = FraudDetectorFactory.getDetector(context = applicationContext)

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val extras = notification.extras

        val packageName = sbn.packageName

        if (isMessage(packageName) || isEmail(packageName)) {


            val title = extras.getString("android.title") ?: "Okänd"
            val normalText = extras.getCharSequence("android.text")?.toString() ?: ""
            val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

            val text = when {
                !bigText.isNullOrBlank() -> bigText
                !normalText.isNullOrBlank() -> normalText
                else -> ""
            }

            if (text.isEmpty()) {
                return
            }


            CoroutineScope(Dispatchers.IO).launch {

                val result = fraudDetector.analyze( text = text)

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

    private fun isMessage(packageName: String) : Boolean{

        return packageName in messageApps
    }

    private fun isEmail(packageName: String): Boolean{

        return packageName in emailApps
    }
}