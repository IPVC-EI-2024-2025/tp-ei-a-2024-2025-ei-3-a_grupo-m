package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.viewModels.BreakdownViewModel
import com.example.project_we_fix_it.viewModels.ChatViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownDetailsScreen(
    breakdownId: String,
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    breakdownViewModel: BreakdownViewModel = hiltViewModel()
) {
    // Load breakdown details when screen is first displayed
    LaunchedEffect(breakdownId) {
        breakdownViewModel.loadBreakdownDetails(breakdownId)
    }

    val breakdown by breakdownViewModel.breakdown.collectAsState()
    val isLoading by breakdownViewModel.isLoading.collectAsState()
    val createdChatId by chatViewModel.createdChatId.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    // show text logic

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cannot Start Chat") },
            text = { Text("Someone needs to be assigned to talk in this chat") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }


    WeFixItAppScaffold(
        title = "Breakdown Details",
        currentRoute = "breakdown_details",
        navController = commonActions.navController,
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
        notificationViewModel = hiltViewModel()
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (breakdown == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Breakdown not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Breakdown Information",
                    style = MaterialTheme.typography.headlineSmall
                )

                val currentUserId = authViewModel.currentUserId
                val assignedTechnician by produceState<String?>(initialValue = null, breakdown) {
                    breakdown?.assignments?.firstOrNull {
                        it.technician_id == currentUserId || it.technician_id != null
                    }?.technician_id?.let { techId ->
                        value = breakdownViewModel.getTechnicianName(techId) ?: "Technician ID: $techId"
                    }
                }

                DetailRow("Assigned to", assignedTechnician ?: "Not assigned")
                DetailRow("Equipment Identification", breakdown?.equipment?.identifier ?: "Unknown equipment")
                DetailRow("Description", breakdown?.description ?: "No description")
                DetailRow("Date", breakdown?.reported_at ?: "Unknown date")
                DetailRow("Location", breakdown?.location ?: "Unknown location")
                DetailRow("Status", breakdown?.status?.replace("_", " ")?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                } ?: "Unknown status")
                DetailRow("Urgency", breakdown?.urgency_level?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
                    ?: "Unknown")

                // Photos section if available
                if (!breakdown?.photos.isNullOrEmpty()) {
                    Text(
                        text = "Photos",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    // Here you would display the photos - implement this based on your image loading solution
                    Text("${breakdown?.photos?.size} photos available")
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            breakdown?.breakdown_id?.let { id ->
                                breakdownViewModel.updateBreakdownUrgencyLevel(id, "critical")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Mark as Critical")
                    }
                    Button(
                        onClick = {
                            breakdown?.let { bd ->
                                val currentUserId = authViewModel.currentUserId
                                if (currentUserId != null) {
                                    val hasAssignedTechnician = bd.assignments.any { it.technician_id != null }

                                    if (hasAssignedTechnician) {
                                        val participants = mutableListOf<String>().apply {
                                            add(currentUserId)
                                            bd.reporter_id?.let { add(it) }
                                            bd.assignments.firstOrNull()?.technician_id?.let { add(it) }
                                        }.distinct()

                                        chatViewModel.createOrGetChat(
                                            breakdownId = bd.breakdown_id,
                                            participants = participants
                                        )
                                    } else {
                                        showDialog = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Chat About This Breakdown")
                    }
                    LaunchedEffect(createdChatId) {
                        createdChatId?.let { chatId ->
                            commonActions.navigateToChat(chatId)
                        }
                    }
                    LaunchedEffect(createdChatId) {
                        createdChatId?.let { chatId ->
                            commonActions.navigateToChat(chatId)
                            // Clear the ID after navigation
                            chatViewModel._createdChatId.value = null
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}