package com.labb.vishinandroid.data.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

/**
 * Hjälparklass som visar en notifikation när tillgänglighetstjänsterna
 * har inaktiverats pga BankID. Notifikationen leder användaren till
 * inställningar för att återaktivera tjänsterna.
 */
object ServiceReEnableHelper {

    private const val CHANNEL_ID = "vishing_reenable_channel"
    private const val NOTIFICATION_ID = 9999

    fun showReEnableNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VishingGuard återaktivering",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Påminnelse om att återaktivera VishingGuard efter BankID-användning"
            }
            manager.createNotificationChannel(channel)
        }


        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("VishingGuard inaktiverad")
            .setContentText("Tryck här för att återaktivera skyddet efter BankID.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("VishingGuard har tillfälligt inaktiverats för att BankID ska fungera. Tryck här för att gå till inställningar och aktivera tjänsterna igen."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }
}
