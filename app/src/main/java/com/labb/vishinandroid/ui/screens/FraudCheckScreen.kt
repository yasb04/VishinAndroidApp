package com.labb.vishinandroid.ui.screens

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.labb.vishinandroid.data.SwedishFraudLocalModel
import com.labb.vishinandroid.data.model.FraudRequest
import com.labb.vishinandroid.data.service.MockFraudDetectionService
import com.labb.vishinandroid.data.service.RecordingService
import com.labb.vishinandroid.repositories.SmsRepository
import com.labb.vishinandroid.ui.theme.VishinAndroidTheme
import kotlinx.coroutines.launch

@Composable
fun FraudCheckScreen(initialMessage: String = "",
                     initialSender: String = "",
                     fraudService: MockFraudDetectionService,
                     modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Krävs för både inspelning och AI-modell

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Enter text to check (Required)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- MANUELL TESTPANEL (Från Main) ---
        Text("🔧 Manuell Testpanel", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, RecordingService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("🔴 Starta Inspelning")
            }

            Button(
                onClick = {
                    val intent = Intent(context, RecordingService::class.java)
                    context.stopService(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("⏹️ Stoppa")
            }
        }
        // --------------------------------------

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (message.isBlank()) {
                    resultText = "Please enter some text."
                    return@Button
                }

                isLoading = true
                resultText = ""
                scope.launch {
                    try {
                        // --- ENSEMBLE AI LOGIK ---
                        val result = SwedishFraudLocalModel.predict(context, message)
                        resultText = buildString {
                            if (result.isFraud) {
                                appendLine("⚠️ VARNING: Misstänkt bedrägeri!")
                                appendLine("Enighet: ${result.votes} av 5 modeller varnar")
                            } else {
                                appendLine("✅ Ser säkert ut")
                                appendLine("Enighet: ${5 - result.votes} av 5 modeller godkänner")
                            }
                            appendLine("Sannolikhet: ${(result.confidence * 100).toInt()}%")
                        }
                    } catch (e: Exception) {
                        resultText = "Error: ${e.message}"
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Check")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (resultText.isNotEmpty()) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Mottagna SMS (Testlogg):", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 8.dp)
        ) {
            items(SmsRepository.smsList) { sms ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "${sms.sender}  •  ${sms.timestamp}", style = MaterialTheme.typography.labelMedium)
                        Text(text = sms.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FraudCheckScreenPreview() {
    VishinAndroidTheme {
        // FraudCheckScreen()
    }
}