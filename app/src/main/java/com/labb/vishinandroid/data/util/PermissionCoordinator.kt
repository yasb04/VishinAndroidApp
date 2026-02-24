package com.labb.vishinandroid.data.util

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
import androidx.lifecycle.viewmodel.compose.viewModel

import com.labb.vishinandroid.ui.screens.FraudCheckScreen
import com.labb.vishinandroid.ui.screens.PermissionScreen
import com.labb.vishinandroid.ui.screens.SecureCallScreen
import com.labb.vishinandroid.ui.vievModel.MainViewModel

@Composable
fun PermissionCoordinator(
    smsSender: String,
    smsMessage: String
    // fraudService togs bort här - ViewModel sköter detta nu via Factory!
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initiera states (endast det som behövs för VoIP-MVP)
    var hasSms by remember { mutableStateOf(PermissionUtils.hasSmsPermission(context)) }
    var hasReadNotif by remember { mutableStateOf(PermissionUtils.hasReadNotificationPermission(context)) }
    var hasPostNotif by remember { mutableStateOf(PermissionUtils.hasPostNotificationPermission(context)) }
    var hasContacts by remember { mutableStateOf(PermissionUtils.hasContactsPermission(context)) }
    var hasPhoneState by remember { mutableStateOf(PermissionUtils.hasPhoneStatePermission(context)) }
    var hasRecordAudio by remember { mutableStateOf(PermissionUtils.hasMicrophonePermission(context)) }
    var hasOverlay by remember { mutableStateOf(PermissionUtils.hasOverlayPermission(context)) }
    var hasCallLog by remember { mutableStateOf(PermissionUtils.hasCallLogPermission(context)) }

    fun refreshAllPermissions() {
        hasSms = PermissionUtils.hasSmsPermission(context)
        hasReadNotif = PermissionUtils.hasReadNotificationPermission(context)
        hasPostNotif = PermissionUtils.hasPostNotificationPermission(context)
        hasContacts = PermissionUtils.hasContactsPermission(context)
        hasPhoneState = PermissionUtils.hasPhoneStatePermission(context)
        hasRecordAudio = PermissionUtils.hasMicrophonePermission(context)
        hasOverlay = PermissionUtils.hasOverlayPermission(context)
        hasCallLog = PermissionUtils.hasCallLogPermission(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshAllPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val allGranted = hasRecordAudio

    var showSecureCall by remember { mutableStateOf(false) }

    if (allGranted) {

        val mainViewModel: MainViewModel = viewModel()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            if (showSecureCall) {
                SecureCallScreen(
                    onBackToFraudCheck = { showSecureCall = false },
                )
            } else {
                FraudCheckScreen(
                    mainViewModel = mainViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onOpenSecureCall = { showSecureCall = true }
                )
            }
        }
    } else {
        PermissionScreen(
            hasSms = hasSms,
            hasReadNotif = hasReadNotif,
            hasPostNotif = hasPostNotif,
            hasContacts = hasContacts,
            hasPhoneState = hasPhoneState,
            hasRecordAudio = hasRecordAudio,
            hasOverlay = hasOverlay,
            hasCallLog = hasCallLog,
            hasAccessibility = true,
            onPermissionResult = { refreshAllPermissions() }
        )
    }
}