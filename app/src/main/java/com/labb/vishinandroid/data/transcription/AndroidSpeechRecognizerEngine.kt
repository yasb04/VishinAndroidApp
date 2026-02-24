package com.labb.vishinandroid.data.transcription

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import com.labb.vishinandroid.data.interfaces.TranscriptionEngine

class AndroidSpeechRecognizerEngine(
    private val application: Application
) : TranscriptionEngine {

    private val _events = MutableSharedFlow<TranscriptionEngine.TranscriptEvent>(
        replay = 0,
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: Flow<TranscriptionEngine.TranscriptEvent> = _events

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening: Boolean = false

    override fun start(languageTag: String) {
        if (!SpeechRecognizer.isRecognitionAvailable(application) || isListening) return

        val recognizer = SpeechRecognizer.createSpeechRecognizer(application)
        speechRecognizer = recognizer

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                isListening = false
            }

            override fun onResults(results: Bundle?) {
                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return
                val text = matches.joinToString(separator = " ")
                _events.tryEmit(
                    TranscriptionEngine.TranscriptEvent(
                        text = text,
                        isFinal = true,
                        isLocalSpeaker = true
                    )
                )
                isListening = false
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?: return
                val text = matches.joinToString(separator = " ")
                _events.tryEmit(
                    TranscriptionEngine.TranscriptEvent(
                        text = text,
                        isFinal = false,
                        isLocalSpeaker = true
                    )
                )
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
        }

        isListening = true
        recognizer.startListening(intent)
    }

    override fun stop() {
        isListening = false
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

