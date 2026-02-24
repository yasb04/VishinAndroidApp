package com.labb.vishinandroid.data.voip

import com.labb.vishinandroid.data.interfaces.VoipEngine

/**
 * Enkel stub som inte gör något VoIP-arbete ännu.
 * Används för att undvika krascher tills WebRTC-konfigurationen är helt korrekt.
 */
class SimpleVoipEngine : VoipEngine {
    override fun startCall() {
        // No-op
    }

    override fun endCall() {
        // No-op
    }
}

