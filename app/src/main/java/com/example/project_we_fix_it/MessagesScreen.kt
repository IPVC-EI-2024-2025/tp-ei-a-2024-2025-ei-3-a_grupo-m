package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.AppNavigator
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Chat
import com.example.project_we_fix_it.supabase.UserProfile
import com.example.project_we_fix_it.viewModels.ChatViewModel
import com.example.project_we_fix_it.viewModels.UserProfileViewModel

@Composable
fun ChatItem(
    chat: Chat,
    participantProfiles: Map<String, UserProfile>,
    currentUserId: String?,
    onClick: () -> Unit
) {
    // Filter out current user from participants
    val otherParticipants = chat.participants.filter { it != currentUserId }
    Log.d("ChatItem", "Other Participants: $otherParticipants")
    val participantNames = otherParticipants.joinToString {
        participantProfiles[it]?.name ?: "Unknown"
    }
    Log.d("ChatItem", "Participant Names: $participantNames")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Participant names
                Text(
                    text = participantNames,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Last message preview
                Text(
                    text = chat.lastMessagePreview ?: "No messages yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            chat.last_message_at?.let {
                Text(
                    text = formatTimestamp(it),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}


fun formatTimestamp(timestamp: String): String {

    return timestamp // Simplified - implement proper formatting
}

@Composable
fun MessagesScreen(
    navigator: AppNavigator,
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()
    val participantProfiles by viewModel.participantProfiles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserId = viewModel.currentUserId

    val error by viewModel.error.collectAsState()
    if (error != null) {
        Log.e("MessagesScreen", "Error: $error")
    }


    LaunchedEffect(Unit) {
        Log.d("MessagesScreen", "Current user ID: $currentUserId")
        viewModel.loadChats(currentUserId)
    }

    WeFixItAppScaffold(
        title = "Messages",
        currentRoute = "messages",
        navController = navigator.navController,
        onNavigateToProfile = commonActions.navigateToProfile,
        onNavigateToHome = commonActions.navigateToHome,
        onOpenSettings = commonActions.openSettings,
        onNavigateToNotifications = commonActions.navigateToNotifications,
        onNavigateToAssignments = commonActions.navigateToAssignments,
        onNavigateToBreakdownReporting = commonActions.navigateToBreakdownReporting,
        onNavigateToMessages = commonActions.navigateToMessages,
        onNavigateToAdminDashboard = commonActions.navigateToAdminDashboard,
        onNavigateToAdminUsers = commonActions.navigateToAdminUsers,
        onNavigateToAdminEquipment = commonActions.navigateToAdminEquipment,
        onNavigateToAdminBreakdowns = commonActions.navigateToAdminBreakdowns,
        onNavigateToAdminAssignments = commonActions.navigateToAdminAssignments,
        onLogout = commonActions.logout,
        authViewModel = authViewModel,
        showBackButton = true,
        onBackClick = commonActions.onBackClick
    ) { padding ->

        Log.d("MessagesScreen", "Chats: $chats")
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No messages yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(chats) { chat ->
                    ChatItem(
                        chat = chat,
                        participantProfiles = participantProfiles,
                        currentUserId = currentUserId,
                        onClick = {
                            chat.chat_id?.let { chatId ->
                                navigator.navigateToChat(chatId)
                            }
                        },
                    )
                }
            }

        }
    }
}