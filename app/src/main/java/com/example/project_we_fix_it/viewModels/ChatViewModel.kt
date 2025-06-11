package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.supabase.Chat
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: SupabaseRepository,
) : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val _createdChatId = MutableStateFlow<String?>(null)
    val createdChatId: StateFlow<String?> = _createdChatId.asStateFlow()

    fun loadChats(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ChatViewModel", "Loading chats for user: $userId")
                _chats.value = repository.getChatsForUser(userId)
                Log.d("ChatViewModel", "Chats loaded: ${_chats.value}")
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createOrGetChat(userId: String, breakdownId: String, technicianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val existingChat = repository.getChatByBreakdownId(breakdownId)
                if (existingChat == null) {
                    val newChat = repository.createChat(
                        breakdownId = breakdownId,
                        participants = listOf(userId, technicianId)
                    )
                    _createdChatId.value = newChat.chat_id
                } else {
                    _createdChatId.value = existingChat.chat_id
                }
                loadChats(userId)
            } catch (e: Exception) {
                _error.value = "Failed to create/get chat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}