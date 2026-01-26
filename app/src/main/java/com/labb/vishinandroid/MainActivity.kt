package com.labb.vishinandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.labb.vishinandroid.ui.screens.FraudCheckScreen
import com.labb.vishinandroid.ui.screens.SmsCheckScreen
import com.labb.vishinandroid.ui.theme.VishinAndroidTheme

class MainActivity : ComponentActivity() {

    // Dessa variabler håller koll på senaste SMS:et
    private var smsSenderState = mutableStateOf("")
    private var smsMessageState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Kolla om vi startades av ett SMS direkt
        handleIncomingIntent(intent)

        setContent {
            VishinAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SmsCheckScreen(
                        initialMessage = smsMessageState.value, // Skicka in SMS-texten
                        initialSender = smsSenderState.value,   // Skicka in Numret
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Denna körs om appen redan är öppen när SMS kommer
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent) // (OBS: I Kotlin heter det Intent utan ?, till skillnad från Java)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            val sender = it.getStringExtra("sender")
            val msg = it.getStringExtra("msg")

            if (sender != null && msg != null) {
                // Uppdatera staten -> Compose kommer automatiskt rita om skärmen!
                smsSenderState.value = sender
                smsMessageState.value = msg
            }
        }
    }
}