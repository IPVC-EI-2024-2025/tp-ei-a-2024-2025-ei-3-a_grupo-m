// ChatScreen.kt
package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    viewModel: ChatViewModel = hiltViewModel()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { navController.navigate("profile") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = { navController.popBackStack("dashboard", false) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, "Notifications") },
                        label = { Text("Notifications") },
                        selected = false,
                        onClick = { navController.navigate("notifications") }
                    )
                }
            }
        }
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
                    Icon(Icons.Default.Send, contentDescription = "Send")
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