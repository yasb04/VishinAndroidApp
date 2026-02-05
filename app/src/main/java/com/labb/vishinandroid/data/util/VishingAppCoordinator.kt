package com.labb.vishinandroid.data.util

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.labb.vishinandroid.data.service.MockFraudDetectionService
import com.labb.vishinandroid.ui.screens.FraudCheckScreen
import com.labb.vishinandroid.ui.screens.PermissionScreen

@Composable
fun VishingAppCoordinator(
    smsSender: String,
    smsMessage: String,
    fraudService: MockFraudDetectionService
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    var hasSms by remember { mutableStateOf(PermissionUtils.hasSmsPermission(context)) }
    var hasNotif by remember { mutableStateOf(PermissionUtils.hasNotificationPermission(context)) }
    var hasContacts by remember { mutableStateOf(PermissionUtils.hasContactsPermission(context))  }
    var hasPhoneState by remember { mutableStateOf(PermissionUtils.hasPhoneStatePermission(context)) }
    var hasRecordAudio by remember { mutableStateOf(PermissionUtils.hasMicrophonePermission(context)) }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasSms = PermissionUtils.hasSmsPermission(context)
                hasNotif = PermissionUtils.hasNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    if (hasSms && hasNotif && hasContacts && hasPhoneState) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            FraudCheckScreen(
                initialMessage = smsMessage,
                initialSender = smsSender,
                fraudService = fraudService,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        PermissionScreen(
            hasSms = hasSms,
            hasNotif = hasNotif,
            hasContacts = hasContacts,
            hasPhoneState = hasPhoneState,
            hasRecordAudio = hasRecordAudio,
            onPermissionResult = {
                hasSms = true
                hasNotif = true
                hasContacts = true
                hasPhoneState = true
                hasRecordAudio = true
            },
        )
    }
}