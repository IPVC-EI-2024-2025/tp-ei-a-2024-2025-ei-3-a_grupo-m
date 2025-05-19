package com.example.project_we_fix_it

data class BreakdownItem(
    val id: String,
    val title: String,
    val description: String,
    val priority: Int = 1
)

data class Message(
    val id: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val isMe: Boolean
)

data class Chat(
    val id: String,
    val participantIds: List<String>,
    val participantNames: List<String>,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0
)