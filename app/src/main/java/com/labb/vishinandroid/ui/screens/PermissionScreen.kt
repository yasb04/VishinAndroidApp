package com.labb.vishinandroid.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionScreen(
    hasSms: Boolean,
    hasNotif: Boolean,
    onSmsPermissionResult: () -> Unit // Callback när SMS är klart
) {
    val context = LocalContext.current

    // Launcher för SMS-popupen
    val smsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onSmsPermissionResult() }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Välkommen till VishingGuard!", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("För att appen ska fungera behöver vi två behörigheter:", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        // KNAPP 1: SMS
        if (!hasSms) {
            Button(
                onClick = { smsLauncher.launch(Manifest.permission.RECEIVE_SMS) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("1. Tillåt SMS (Popup)")
            }
        } else {
            Text(" SMS-åtkomst klar!", color = Color.Green)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KNAPP 2: NOTIFIKATIONER
        if (!hasNotif) {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
            ) {
                Text("2. Tillåt Notiser (Inställningar)")
            }
        } else {
            Text(" Notis-åtkomst klar!", color = Color.Green)
        }
    }
}