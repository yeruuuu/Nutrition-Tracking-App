// GenAIViewModel.kt
package com.fit2081.a3_racheltham.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.a3_racheltham.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/** Represents loading / success / error states of a single AI tip. */
sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val text: String) : UiState()
    data class Error(val message: String) : UiState()
}

class GenAIViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey     = BuildConfig.GEMINI_API_KEY
    )

    /** Fire off a prompt to Gemini */
    fun sendPrompt(prompt: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withTimeout(10_000) {
                val response = generativeModel.generateContent(
                    content { text(prompt) }
                )
                val out = response.text ?: throw Exception("No response text")
                _uiState.value = UiState.Success(out.trim()) }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = UiState.Error("Request timed out.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun generateDataPatterns(patientsJson: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = buildString {
                    appendLine("I have a HEIFA dataset in JSON, please read it and return **exactly three** concise bullet-point insights about data patterns (e.g. correlations, gender differences).")
                    appendLine()
                    append(patientsJson)
                }

                val response = generativeModel.generateContent(
                    content { text(prompt) }
                ).text?.trim()
                    ?: throw Exception("Empty AI response")

                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
