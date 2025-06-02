// ChatScreen.kt
package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    WeFixItAppScaffold(
        title = "Chat",
        currentRoute = "chat",
        navController = navController,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onOpenSettings = onOpenSettings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAssignments = onNavigateToAssignments,
        onNavigateToBreakdownReporting = onNavigateToBreakdownReporting,
        onLogout = onLogout,
        authViewModel = authViewModel,
        showBackButton = true,
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.messageText,
                    onValueChange = viewModel::onMessageChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter Message") }
                )
                IconButton(
                    onClick = { viewModel.sendMessage(chatId) },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (message.isMe) 8.dp else 0.dp,
                        bottomEnd = if (message.isMe) 0.dp else 8.dp
                    )
                )
                .background(
                    if (message.isMe) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Column {
                if (!message.isMe) {
                    Text(
                        text = message.senderName,
                        color = if (message.isMe) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.content,
                    color = if (message.isMe) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}