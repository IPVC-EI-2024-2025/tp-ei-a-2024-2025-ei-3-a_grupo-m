package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.Chat
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _participantProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val participantProfiles: StateFlow<Map<String, UserProfile>> = _participantProfiles.asStateFlow()

    private val _breakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val breakdowns: StateFlow<List<Breakdown>> = _breakdowns.asStateFlow()


    val currentUserId = authRepository.getCurrentUser()?.id


    val _createdChatId = MutableStateFlow<String?>(null)
    val createdChatId: StateFlow<String?> = _createdChatId.asStateFlow()

    private val profileManager = ProfileManager(repository)

    fun loadChats(currentUserId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                _breakdowns.value = repository.getAllBreakdowns()
                // Get current user ID
                val userId = currentUserId ?: return@launch

                Log.d("ChatViewModel", "Loading chats for user: $userId")

                // Fetch chats
                val chats = repository.getChatsForUser(userId)
                _chats.value = chats

                Log.d("ChatViewModel", "Chats fetched: $chats")

                // Fetch participant profiles
                val participantIds = chats.flatMap { it.participants }.toSet()
                val profiles = mutableMapOf<String, UserProfile>()

                Log.d("ChatViewModel", "Participant IDs: $participantIds")

                participantIds.forEach { id ->
                    repository.getUserProfile(id)?.let { profile ->
                        profiles[id] = profile
                    }
                }

                _participantProfiles.value = profiles

                Log.d("ChatViewModel", "Participant profiles fetched: $profiles")

            } catch (e: Exception) {
                _error.value = "Failed to load chats: ${e.message}"
                Log.e("ChatViewModel", "Error loading chats: ${e.message}", e)
            } finally {
                _isLoading.value = false
                Log.d("ChatViewModel", "Chats loaded")
            }
        }
    }

    fun createOrGetChat(breakdownId: String?, participants: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ChatViewModel", "Creating or getting chat for breakdown ID: $breakdownId")
            try {
                val existingChat = breakdownId?.let {
                    repository.getChatByBreakdownId(it)
                }
                Log.d("ChatViewModel", "Existing chat: $existingChat")
                if (existingChat == null) {
                    val newChat = repository.createChat(
                        breakdownId = breakdownId,
                        participants = participants
                    )
                    Log.d("ChatViewModel", "New chat created: $newChat")
                    _createdChatId.value = newChat.chat_id
                } else {
                    _createdChatId.value = existingChat.chat_id
                    Log.d("ChatViewModel", "Existing chat returned: $existingChat")
                }
            } catch (e: Exception) {
                _error.value = "Failed to create/get chat: ${e.message}"
                Log.e("ChatViewModel", "Error creating/getting chat: ${e.message}", e)
            } finally {
                _isLoading.value = false
                Log.d("ChatViewModel", "Chat creation/retrieval completed")
            }
        }
    }
    suspend fun getUserProfile(userId: String?): UserProfile? {
        if (userId == null) return null
        return viewModelScope.run {
            profileManager.getProfile(userId)
        }
    }

}