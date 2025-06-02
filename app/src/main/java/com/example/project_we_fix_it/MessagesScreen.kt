package com.example.project_we_fix_it

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.participantNames.filter { it != "You" }.joinToString(),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.lastMessageTime,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = chat.lastMessage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navController: NavController,
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
    val chats by viewModel.chats.collectAsState()

    WeFixItAppScaffold(
        title = "Messages",
        currentRoute = "messages",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chats) { chat ->
                ChatItem(chat = chat) {
                    navController.navigate("chat/${chat.id}")
                }
            }
        }
    }
}