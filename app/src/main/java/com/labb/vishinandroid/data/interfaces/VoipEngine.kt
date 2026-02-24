package com.labb.vishinandroid.data.interfaces

interface VoipEngine {

    enum class CallState {
        Idle,
        Connecting,
        Active
    }

    fun startCall()

    fun endCall()
}

