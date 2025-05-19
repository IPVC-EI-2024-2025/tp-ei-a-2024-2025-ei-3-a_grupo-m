// ChatViewModel.kt
package com.example.project_we_fix_it

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    var messageText by mutableStateOf("")
        private set

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _chats.value = listOf(
                Chat(
                    id = "1",
                    participantIds = listOf("1", "2"),
                    participantNames = listOf("You", "Colega1"),
                    lastMessage = "Supporting line text",
                    lastMessageTime = "Today"
                ),
                Chat(
                    id = "2",
                    participantIds = listOf("1", "3"),
                    participantNames = listOf("You", "User31231"),
                    lastMessage = "Supporting line text",
                    lastMessageTime = "Today"
                ),
                Chat(
                    id = "3",
                    participantIds = listOf("1", "4"),
                    participantNames = listOf("You", "Boss"),
                    lastMessage = "Supporting line text",
                    lastMessageTime = "Today"
                )
            )
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            _messages.value = listOf(
                Message(
                    id = "1",
                    senderId = "1",
                    senderName = "You",
                    content = "Hello, did you solve the breakdown?",
                    timestamp = System.currentTimeMillis() - 10000,
                    isMe = true
                ),
                Message(
                    id = "2",
                    senderId = "2",
                    senderName = "Colega1",
                    content = "Yes, it was quick to fix",
                    timestamp = System.currentTimeMillis() - 5000,
                    isMe = false
                )
            )
        }
    }

    fun onMessageChange(newText: String) {
        messageText = newText
    }

    fun sendMessage(chatId: String) {
        if (messageText.isNotBlank()) {
            viewModelScope.launch {
                val newMessage = Message(
                    id = (messages.value.size + 1).toString(),
                    senderId = "1",
                    senderName = "You",
                    content = messageText,
                    timestamp = System.currentTimeMillis(),
                    isMe = true
                )
                _messages.value = _messages.value + newMessage
                messageText = ""
            }
        }
    }
}