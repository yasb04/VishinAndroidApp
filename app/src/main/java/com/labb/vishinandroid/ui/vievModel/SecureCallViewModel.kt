package com.labb.vishinandroid.ui.vievModel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.labb.vishinandroid.data.factories.FraudDetectorFactory
import com.labb.vishinandroid.data.interfaces.FraudDetector
import com.labb.vishinandroid.data.service.SecureCallService
import com.labb.vishinandroid.data.transcription.AndroidSpeechRecognizerEngine
import com.labb.vishinandroid.data.interfaces.TranscriptionEngine
import com.labb.vishinandroid.data.interfaces.VoipEngine
import com.labb.vishinandroid.data.voip.WebRtcVoipEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecureCallViewModel(application: Application) : AndroidViewModel(application) {

    enum class CallStatus {
        Idle,
        Connecting,
        Active
    }

    private val _callStatus = MutableStateFlow(CallStatus.Idle)
    val callStatus = _callStatus.asStateFlow()

    private val _localTranscript = MutableStateFlow("")
    val localTranscript = _localTranscript.asStateFlow()

    private val _remoteTranscript = MutableStateFlow("")
    val remoteTranscript = _remoteTranscript.asStateFlow()

    private val transcriptionEngine: TranscriptionEngine =
        AndroidSpeechRecognizerEngine(application)

    private val voipEngine: VoipEngine = WebRtcVoipEngine(application)

    private val fraudDetector: FraudDetector =
        FraudDetectorFactory.getDetector(application)

    private val _fraudWarnings = MutableStateFlow<List<String>>(emptyList())
    val fraudWarnings = _fraudWarnings.asStateFlow()

    fun startCall() {
        if (_callStatus.value != CallStatus.Idle) return

        _callStatus.value = CallStatus.Connecting

        val context = getApplication<Application>()
        val intent = Intent(context, SecureCallService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        voipEngine.startCall()

        viewModelScope.launch {
            transcriptionEngine.events.collect { event ->
                if (event.isLocalSpeaker) {
                    _localTranscript.value = event.text
                } else {
                    _remoteTranscript.value = event.text
                }

                if (event.isFinal && event.text.isNotBlank()) {
                    val text = event.text
                    viewModelScope.launch {
                        try {
                            val result = fraudDetector.analyze(text)
                            if (result.isFraud) {
                                val updated = _fraudWarnings.value + text
                                _fraudWarnings.value = updated
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }

        transcriptionEngine.start(languageTag = "sv-SE")
        _callStatus.value = CallStatus.Active
    }

    fun endCall() {
        if (_callStatus.value == CallStatus.Idle) return

        val context = getApplication<Application>()
        val intent = Intent(context, SecureCallService::class.java)
        context.stopService(intent)

        transcriptionEngine.stop()
        voipEngine.endCall()
        _callStatus.value = CallStatus.Idle
    }
}

