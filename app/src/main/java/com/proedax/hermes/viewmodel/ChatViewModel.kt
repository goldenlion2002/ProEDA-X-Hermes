package com.proedax.hermes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proedax.hermes.ai.HermesClient
import com.proedax.hermes.db.ConversationEntity
import com.proedax.hermes.db.MessageEntity
import com.proedax.hermes.settings.SettingsManager
import com.proedax.hermes.ui.chat.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val hermesClient: HermesClient,
    private val settingsManager: SettingsManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsManager.getSettings()
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val userMessage = Message(content, true, System.currentTimeMillis())
            _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + userMessage)

            val messages = listOf(
                mapOf("role" to "user", "content" to content)
            )

            val response = hermesClient.sendMessage(messages)
            when (response) {
                is com.proedax.hermes.ai.AiResponse.Success -> {
                    val assistantMessage = Message(response.content ?: "No response", false, System.currentTimeMillis())
                    _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + assistantMessage)
                }
                is com.proedax.hermes.ai.AiResponse.Error -> {
                    val errorMessage = Message("Error: ${response.message}", false, System.currentTimeMillis())
                    _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + errorMessage)
                }
            }
        }
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)