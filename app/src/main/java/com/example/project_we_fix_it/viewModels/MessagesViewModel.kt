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

    var currentBreakdownId: String? = null
    var currentChatId: String? = null

    fun loadMessages(chatId: String? = null, breakdownId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (chatId != null) {
                    currentChatId = chatId
                    currentBreakdownId = breakdownId // Store the breakdown ID too
                    _messages.value = repository.getMessagesByChat(chatId) ?:
                            if (breakdownId != null) repository.getMessagesByBreakdown(breakdownId)
                            else emptyList()
                } else if (breakdownId != null) {
                    currentBreakdownId = breakdownId
                    _messages.value = repository.getMessagesByBreakdown(breakdownId)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            try {
                if (content.isBlank()) return@launch

                if (currentChatId != null) {
                    val message = Message(
                        chat_id = currentChatId,
                        breakdown_id = currentBreakdownId,
                        content = content,
                        sender_id = senderId
                    )
                    repository.sendMessage(message)
                    loadMessages(chatId = currentChatId)
                }
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error sending message", e)
            }
        }
    }
}