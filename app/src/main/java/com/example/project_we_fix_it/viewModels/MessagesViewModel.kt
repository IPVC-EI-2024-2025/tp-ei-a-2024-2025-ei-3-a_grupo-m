package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.Message
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var currentChatId: String? = null
    var currentBreakdownId: String? = null

    fun loadMessages(chatId: String?, breakdownId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentChatId = chatId
                currentBreakdownId = breakdownId

                val newMessages = when {
                    chatId != null -> repository.getMessagesByChat(chatId)
                    breakdownId != null -> repository.getMessagesByBreakdown(breakdownId)
                    else -> emptyList()
                }
                _messages.value = newMessages
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error loading messages", e)
                _error.value = "Failed to load messages: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (content.isBlank()) {
                    _isLoading.value = false
                    return@launch
                }

                // Create message without ID or timestamp - Supabase will generate these
                val message = Message(
                    chat_id = currentChatId,
                    breakdown_id = currentBreakdownId,
                    content = content,
                    sender_id = senderId,
                    message_id = null // Let Supabase generate the ID
                )

                Log.d("MessagesViewModel", "Attempting to send message: $message")

                // Send to server and get the returned message with generated ID
                val sentMessage = repository.sendMessage(message)

                Log.d("MessagesViewModel", "Message sent successfully: ${sentMessage.message_id}")

                // Add the sent message to the UI immediately
                val currentMessages = _messages.value
                _messages.value = currentMessages + sentMessage

                Log.d("MessagesViewModel", "Messages count after send: ${_messages.value.size}")

            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Failed to send message", e)
                Log.e("MessagesViewModel", "Error details: ${e.stackTraceToString()}")

                // Check if the message was actually sent despite the error
                if (e.message?.contains("Expected start of the array") == true ||
                    e.message?.contains("JSON") == true) {
                    Log.w("MessagesViewModel", "Possible Supabase response parsing issue, reloading messages...")
                    // Reload messages to get the latest state from server
                    loadMessages(currentChatId, currentBreakdownId)
                } else {
                    _error.value = "Failed to send message: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}