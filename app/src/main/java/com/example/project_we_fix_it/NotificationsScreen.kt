package com.example.project_we_fix_it

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Notification
import com.example.project_we_fix_it.viewModels.AssignmentViewModel
import com.example.project_we_fix_it.viewModels.NotificationViewModel
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    assignmentViewModel: AssignmentViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val userId = authState.user?.id
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showCompleteDialog = remember { mutableStateOf<Notification?>(null) }

    LaunchedEffect(userId) {
        Log.d("NotificationsScreen", "UserId changed: $userId")
        userId?.let {
            notificationViewModel.loadNotifications(it)
        } ?: run {
            Log.w("NotificationsScreen", "No user ID available")
        }
    }

    LaunchedEffect(notifications) {
        Log.d("NotificationsScreen", "Notifications: ${notifications.size}, Unread: $unreadCount")
        notifications.forEach {
            Log.d("NotificationsScreen", "Notification: ${it.title} - ${it.message}")
        }
    }

    LaunchedEffect(notifications) {
        Log.d("NotificationsScreen", "Notifications updated. Count: ${notifications.size}")
        if (notifications.isNotEmpty()) {
            Log.d("NotificationsScreen", "First notification: ${notifications.first().title}")
            Log.d("NotificationsScreen", "Unread count: $unreadCount")
        } else {
            Log.d("NotificationsScreen", "No notifications found")
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete all notifications?") },
            text = { Text("This will permanently remove all your notifications.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        userId?.let { notificationViewModel.deleteAllNotifications(it) }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    showCompleteDialog.value?.let { notification ->
        AlertDialog(
            onDismissRequest = { showCompleteDialog.value = null },
            title = { Text("Mark breakdown as complete?") },
            text = {
                Column {
                    Text(notification.message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This will permanently close the breakdown.",
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        notification.related_id?.let { breakdownId ->
                            val metadata = try {
                                Json.decodeFromString<Map<String, String>>(
                                    notification.metadata ?: "{}"
                                )
                            } catch (e: Exception) {
                                emptyMap()
                            }

                            val technicianId = metadata["technician_id"] ?: ""

                            adminViewModel.completeBreakdown(breakdownId, technicianId)
                        }
                        showCompleteDialog.value = null
                    }
                ) {
                    Text("Confirm Complete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCompleteDialog.value = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifications ${if (unreadCount > 0) "($unreadCount)" else ""}")
                },
                actions = {
                    IconButton(
                        onClick = {
                            userId?.let {
                                notificationViewModel.loadNotifications(it)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    if (notifications.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                userId?.let { id ->
                                    notificationViewModel.markAllAsRead(id)
                                }
                            }
                        ) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Mark all as read")
                        }

                        IconButton(
                            onClick = { showDeleteDialog.value = true }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_all_confirmation))
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            notifications.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_notifications))
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications.sortedByDescending { it.created_at }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                when {
                                    notification.title.contains("Breakdown", ignoreCase = true) -> {
                                        notification.related_id?.let { id ->
                                            commonActions.navigateToBreakdownDetails(id)
                                        }
                                    }
                                    notification.title.contains("Complete Request", ignoreCase = true) -> {
                                        showCompleteDialog.value = notification
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val backgroundColor = if (!notification.read) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                notification.created_at?.let {
                    Text(
                        text = formatSupabaseTimestamp(it),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )

            notification.metadata?.let { meta ->
                val metadata = try {
                    Json.decodeFromString<Map<String, String>>(meta)
                } catch (e: Exception) {
                    null
                }

                metadata?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column {
                        if (it.containsKey("by")) {
                            Text(
                                text = "By: ${it["by"]}",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        it.filter { (key, _) -> key != "by" }.forEach { (key, value) ->
                            Text(
                                text = "$key: $value",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatSupabaseTimestamp(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        timestamp
    }
}

