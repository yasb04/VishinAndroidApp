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

    // Använd vår nya PermissionUtils-fil
    var hasSms by remember { mutableStateOf(PermissionUtils.hasSmsPermission(context)) }
    var hasNotif by remember { mutableStateOf(PermissionUtils.hasNotificationPermission(context)) }

    // Lyssnare som kollar behörigheter när man kommer tillbaka till appen
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

    // Välj vilken skärm som ska visas
    if (hasSms && hasNotif) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Här "injicerar" vi datan och servicen till skärmen
            FraudCheckScreen(
                initialMessage = smsMessage,
                initialSender = smsSender,
                fraudService = fraudService, // <--- Här skickas den in
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        // Visa den separata permission-skärmen
        PermissionScreen(
            hasSms = hasSms,
            hasNotif = hasNotif,
            onSmsPermissionResult = { hasSms = true }
        )
    }
}