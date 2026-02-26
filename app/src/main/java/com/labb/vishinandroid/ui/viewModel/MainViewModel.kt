package com.labb.vishinandroid.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.labb.vishinandroid.data.factories.FraudDetectorFactory
import com.labb.vishinandroid.domain.interfaces.FraudDetector
import com.labb.vishinandroid.domain.repositories.CallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _fraudCheckResult = MutableStateFlow<String>("")
    val fraudCheckResult = _fraudCheckResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val fraudDetector: FraudDetector = FraudDetectorFactory.getDetector(getApplication())


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
                        appendLine("VARNING: Misstänkt bedrägeri!")

                    } else {
                        appendLine("Ser säkert ut")
                    }
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