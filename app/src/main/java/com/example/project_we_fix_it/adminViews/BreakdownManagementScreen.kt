package com.example.project_we_fix_it.adminViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.viewModels.admin.BreakdownManagementViewModel
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownManagementScreen(
    commonActions: CommonScreenActions,
    viewModel: BreakdownManagementViewModel = hiltViewModel()
) {
    val breakdowns by viewModel.filteredBreakdowns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val showTechnicianDialog by viewModel.showTechnicianDialog.collectAsState()
    val selectedBreakdown by viewModel.selectedBreakdown.collectAsState()

    WeFixItAppScaffold(
        title = "Breakdown Management",
        currentRoute = "admin/breakdowns",
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
        showBackButton = true,
        onBackClick = commonActions.onBackClick,
        actions = {
            // Filter dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                        contentDescription = "Filter"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Breakdowns") },
                        onClick = {
                            viewModel.applyFilter("all")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Open") },
                        onClick = {
                            viewModel.applyFilter("open")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("In Progress") },
                        onClick = {
                            viewModel.applyFilter("in_progress")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Completed") },
                        onClick = {
                            viewModel.applyFilter("completed")
                            expanded = false
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5C5CFF))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Filter indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing: ${filterStatus.replace("_", " ").replaceFirstChar { it.uppercase() }}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${breakdowns.size} breakdowns",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(breakdowns) { breakdown ->
                            BreakdownCard(
                                breakdown = breakdown,
                                onClick = {
                                    viewModel.selectBreakdown(breakdown)
                                    commonActions.navigateToBreakdownDetails(breakdown.breakdown_id)
                                },
                                onAssignClick = {
                                    viewModel.selectBreakdown(breakdown)
                                    viewModel.showTechnicianDialog(true)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTechnicianDialog && selectedBreakdown != null) {
        TechnicianAssignmentDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showTechnicianDialog(false) }
        )
    }
}

@Composable
private fun BreakdownCard(
    breakdown: Breakdown,
    onClick: () -> Unit,
    onAssignClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = breakdown.equipment_id ?: "No Equipment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Status chip
                Box(
                    modifier = Modifier
                        .background(
                            color = when (breakdown.status) {
                                "open" -> Color(0xFFFFA726)
                                "in_progress" -> Color(0xFF42A5F5)
                                "completed" -> Color(0xFF66BB6A)
                                else -> Color.Gray
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = breakdown.status.replace("_", " ").replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = breakdown.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Urgency: ${breakdown.urgency_level.replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                if (breakdown.status == "open") {
                    Button(
                        onClick = onAssignClick,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C5CFF),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Assign",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TechnicianAssignmentDialog(
    viewModel: BreakdownManagementViewModel,
    onDismiss: () -> Unit
) {
    val technicians by viewModel.technicians.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Technician") },
        text = {
            Column {
                Text("Select a technician to assign to this breakdown")
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(technicians) { technician ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedBreakdown.value?.let { breakdown ->
                                        viewModel.assignTechnician(breakdown.breakdown_id, technician.user_id)
                                    }
                                    onDismiss()
                                }
                                .padding(4.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = technician.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = technician.location ?: "No location",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select"
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}