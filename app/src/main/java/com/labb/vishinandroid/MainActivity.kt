package com.labb.vishinandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.labb.vishinandroid.data.service.MockFraudDetectionService
import com.labb.vishinandroid.data.util.PermissionCoordinator
import com.labb.vishinandroid.ui.theme.VishinAndroidTheme

class MainActivity : ComponentActivity() {


    private var smsSenderState = mutableStateOf("-")
    private var smsMessageState = mutableStateOf("-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)

        setContent {
            VishinAndroidTheme {

                PermissionCoordinator(
                    smsSender = smsSenderState.value,
                    smsMessage = smsMessageState.value
                )
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            val sender = it.getStringExtra("sender")
            val msg = it.getStringExtra("msg")
            if (sender != null && msg != null) {
                smsSenderState.value = sender
                smsMessageState.value = msg
            }
        }
    }
}

