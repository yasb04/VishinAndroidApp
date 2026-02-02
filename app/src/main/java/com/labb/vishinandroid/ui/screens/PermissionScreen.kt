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
    hasContacts: Boolean,
    hasPhoneState: Boolean,
    onPermissionResult: () -> Unit
) {
    val context = LocalContext.current


    val smsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionResult() }
    )

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) { onPermissionResult}
        }
    )

    val phoneStateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionResult() }
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
                "För att appen ska fungera behöver vi fyra behörigheter:",
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

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasContacts) {
                Button(
                    onClick = { contactLauncher.launch(Manifest.permission.READ_CONTACTS) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text("3. Tillåt kontakt-åtkomst")
                }
            } else {
                Text(" kontakt-åtkomst klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (!hasPhoneState) {
                Button(
                    onClick = { phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text("4. Tillåt Telefonstatus-åtkomst")
                }
            } else {
                Text(" telefonstatus-åtkomst klar!", color = Color(0xFFFFFFFF))
            }
        }
    }
}