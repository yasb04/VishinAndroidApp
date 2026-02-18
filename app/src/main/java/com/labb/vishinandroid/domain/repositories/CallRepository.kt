package com.labb.vishinandroid.domain.repositories

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList // Viktig import
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CallDialogue(
    val text: String,
    val timestamp: String,
    val isFraud: Boolean
)

data class CallSession(
    val phoneNumber: String,
    val startTime: String,
    // ÄNDRING 1: Byt till SnapshotStateList för att UI ska uppdateras live
    val dialogue: SnapshotStateList<CallDialogue> = mutableStateListOf(),
    // ÄNDRING 2: Byt till MutableState för att färgändring (vid fraud) ska ske direkt
    var isFraudulentCall: MutableState<Boolean> = mutableStateOf(false)
)

object CallRepository {
    private const val TAG = "VishingGuard_DEBUG"

    // Huvudlistan måste också vara en StateList (det har du redan, vilket är bra)
    val sessions = mutableStateListOf<CallSession>()

    private var currentSession: CallSession? = null

    fun startNewSession(number: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val newSession = CallSession(phoneNumber = number, startTime = time)
        currentSession = newSession
        sessions.add(0, newSession)
        Log.d(TAG, "Ny samtalssession startad: $number kl $time")
    }

    fun addTextToActiveSession(text: String, isFraud: Boolean) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        if (currentSession == null) {
            Log.w(TAG, "Försökte logga text men ingen session aktiv. Skapar nödsession.")
            startNewSession("Okänt samtal")
        }

        currentSession?.let {
            // Nu när 'dialogue' är en SnapshotStateList kommer UI:t ritas om automatiskt
            it.dialogue.add(CallDialogue(text, timestamp, isFraud))

            if (isFraud) {
                // Uppdatera state-variabeln
                it.isFraudulentCall.value = true
            }
            Log.d(TAG, "Loggat i session [${it.phoneNumber}]: $text (Fraud: $isFraud)")
        }
    }
}