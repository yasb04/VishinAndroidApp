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
    onSmsPermissionResult: () -> Unit
) {
    val context = LocalContext.current


    val smsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onSmsPermissionResult() }
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Välkommen till VishingGuard!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "För att appen ska fungera behöver vi två behörigheter:",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!hasSms) {
                Button(
                    onClick = { smsLauncher.launch(Manifest.permission.RECEIVE_SMS) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text("1. Tillåt SMS-åtkomst")
                }
            } else {
                Text(" SMS-åtkomst klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasNotif) {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text("2. Tillåt Notis-åtkomst")
                }
            } else {
                Text(" Notis-åtkomst klar!", color = Color(0xFFFFFFFF))
            }
        }
    }
}