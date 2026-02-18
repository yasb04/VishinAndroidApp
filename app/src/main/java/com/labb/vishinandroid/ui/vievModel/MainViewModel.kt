package com.labb.vishinandroid.ui.vievModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.labb.vishinandroid.data.factories.FraudDetectorFactory // Använd Factory
import com.labb.vishinandroid.data.interfaces.FraudDetector     // Använd Interface
import com.labb.vishinandroid.domain.repositories.CallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _fraudCheckResult = MutableStateFlow<String>("")
    val fraudCheckResult = _fraudCheckResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Hämta instansen via Factory. Nu är vi helt frikopplade från specifika klasser!
    private val fraudDetector: FraudDetector = FraudDetectorFactory.getDetector(getApplication())

    val callSessions = CallRepository.sessions

    fun analyzeText(text: String) {
        if (text.isBlank()) {
            _fraudCheckResult.value = "Please enter some text."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _fraudCheckResult.value = ""
            try {

                val result = fraudDetector.analyze(text)




                _fraudCheckResult.value = buildString {
                    if (result.isFraud) {
                        appendLine("⚠️ VARNING: Misstänkt bedrägeri!")

                    } else {
                        appendLine("✅ Ser säkert ut")
                    }
                    // Visa procent med 0 decimaler
                    appendLine("Sannolikhet: ${(result.score * 100).toInt()}%")
                }
            } catch (e: Exception) {
                _fraudCheckResult.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}