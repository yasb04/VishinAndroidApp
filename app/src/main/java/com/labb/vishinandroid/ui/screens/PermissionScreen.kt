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
    hasReadNotif: Boolean,
    hasPostNotif: Boolean,
    hasContacts: Boolean,
    hasPhoneState: Boolean,
    hasRecordAudio: Boolean,
    hasOverlay: Boolean,
    hasCallLog: Boolean,
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

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionResult() }
    )


    val phoneStateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionResult() }
    )

    val postNotifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionResult() }
    )

    val callLogLauncher = rememberLauncherForActivityResult(
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
                "För att appen ska fungera behöver vi några behörigheter:",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!hasSms) { //sms knapp
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

            if (!hasReadNotif) { //läs notifierings knapp
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

            if (!hasPostNotif) {// skicka notifierings knapp
                Button(
                    onClick = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            postNotifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onPermissionResult() // Behövs ej på äldre telefoner
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) { Text("Tillåt att SKICKA notiser") }
            } else {
                Text("Skicka notiser klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasContacts) { // kontakt knapp
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


            if (!hasPhoneState) { //phone state knapp
                Button(
                    onClick = { phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Text("4. Tillåt Telefonstatus-åtkomst")
                }
            } else {
                Text(" telefonstatus-åtkomst klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasRecordAudio) { // Mikrofon knapp
                Button(
                    onClick = { recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)) // Röd för att visa att den är viktig
                ) {
                    Text("5. Tillåt Mikrofon (Inspelning)")
                }
            } else {
                Text(" Mikrofon-åtkomst klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasOverlay) { //overlay knapp
                Button(
                    onClick = {
                        // Denna permission kräver en speciell Intent
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        // intent.data = Uri.parse("package:${context.packageName}") // Valfritt: öppnar direkt för din app
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)) // Lila
                ) {
                    Text("6. Tillåt 'Visa över andra appar' (Fixar krasch)")
                }
            } else {
                Text("Overlay-åtkomst klar!", color = Color(0xFFFFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasCallLog) {
                Button(
                    onClick = { callLogLauncher.launch(Manifest.permission.READ_CALL_LOG) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                ) { Text("Tillåt Samtalslogg (Se nummer)") }
            } else {
                Text("Samtalslogg klar!", color = Color(0xFFFFFFFF))
            }
        }
    }
}