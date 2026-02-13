package com.labb.vishinandroid.data.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasReadNotificationPermission(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        val packageName = context.packageName
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

    fun hasPostNotificationPermission(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    fun hasCallLogPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPhoneStatePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasMicrophonePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
