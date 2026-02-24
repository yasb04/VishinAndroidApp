package com.labb.vishinandroid.data.interfaces

import kotlinx.coroutines.flow.Flow

interface TranscriptionEngine {

    data class TranscriptEvent(
        val text: String,
        val isFinal: Boolean,
        val isLocalSpeaker: Boolean
    )

    val events: Flow<TranscriptEvent>

    fun start(languageTag: String)

    fun stop()
}

