package com.example.project_we_fix_it.adminViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.UserProfile
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownManagementScreen(
    commonActions: CommonScreenActions,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val breakdowns by viewModel.breakdowns.collectAsState()
    val users by viewModel.users.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filterStatus by remember { mutableStateOf("all") }
    var showTechnicianDialog by remember { mutableStateOf(false) }
    var selectedBreakdown by remember { mutableStateOf<Breakdown?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Filter breakdowns based on selected status
    val filteredBreakdowns = remember(breakdowns, filterStatus) {
        when (filterStatus) {
            "all" -> breakdowns
            else -> breakdowns.filter { it.status == filterStatus }
        }
    }

    // Get technicians (active users with technician role)
    val technicians = remember(users) {
        users.filter { it.role == "technician" && it.status == "active" }
    }

    // Load data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

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
        notificationViewModel = hiltViewModel(),
        actions = {
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Breakdown"
                )
            }

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
                            filterStatus = "all"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Open") },
                        onClick = {
                            filterStatus = "open"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("In Progress") },
                        onClick = {
                            filterStatus = "in_progress"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Completed") },
                        onClick = {
                            filterStatus = "completed"
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
                            text = "${filteredBreakdowns.size} breakdowns",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredBreakdowns) { breakdown ->
                            BreakdownCard(
                                breakdown = breakdown,
                                onClick = {
                                    breakdown.breakdown_id?.let {
                                        commonActions.navigateToBreakdownDetails(it)
                                    }
                                },
                                onAssignClick = {
                                    selectedBreakdown = breakdown
                                    showTechnicianDialog = true
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTechnicianDialog && selectedBreakdown != null) {
        TechnicianAssignmentDialog(
            technicians = technicians,
            onDismiss = { showTechnicianDialog = false },
            onAssign = { technicianId ->
                selectedBreakdown?.let { breakdown ->
                    // Update breakdown status to in_progress
                    val updatedBreakdown = breakdown.copy(status = "in_progress")
                    viewModel.updateBreakdown(updatedBreakdown)

                    // Create assignment
                    val assignment = Assignment(
                        breakdown_id = breakdown.breakdown_id,
                        technician_id = technicianId,
                        status = "active"
                    )
                    viewModel.createAssignment(assignment)
                }
                showTechnicianDialog = false
                selectedBreakdown = null
            },
            viewModel = viewModel, // Passe o viewModel
            breakdown = selectedBreakdown!! // Passe a avaria selecionada
        )
    }

    if (showCreateDialog) {
        BreakdownCreateDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onCreate = { breakdown ->
                viewModel.createBreakdown(breakdown)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun BreakdownCard(
    breakdown: Breakdown,
    onClick: () -> Unit,
    onAssignClick: () -> Unit,
    viewModel: AdminViewModel
) {
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()
    val currentEquipment = equipment.find { it.equipment_id == breakdown.equipment_id }

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
                Column {
                    Text(
                        text = currentEquipment?.identifier ?: "No Equipment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = breakdown.description.take(30),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                // Status dropdown
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Box(
                        modifier = Modifier
                            .clickable { expanded = true }
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

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("open", "in_progress", "completed").forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(status.replace("_", " ").replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    viewModel.updateBreakdown(breakdown.copy(status = status))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

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
    technicians: List<UserProfile>,
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit,
    viewModel: AdminViewModel,
    breakdown: Breakdown
) {
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
                                    onAssign(technician.user_id)
                                    viewModel.createNotificationForAssignment(
                                        technicianId = technician.user_id,
                                        breakdownId = breakdown.breakdown_id ?: "",
                                        breakdownDescription = breakdown.description
                                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownCreateDialog(
    viewModel: AdminViewModel,
    onDismiss: () -> Unit,
    onCreate: (Breakdown) -> Unit
) {
    var selectedEquipmentId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("low") }
    var location by remember { mutableStateOf("") }

    // Dropdown expansion states
    var equipmentExpanded by remember { mutableStateOf(false) }
    var urgencyExpanded by remember { mutableStateOf(false) }

    // Get equipment data from viewModel
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()

    val urgencyLevels = listOf("low", "medium", "high")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report New Breakdown") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Equipment dropdown
                ExposedDropdownMenuBox(
                    expanded = equipmentExpanded,
                    onExpandedChange = { equipmentExpanded = !equipmentExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = selectedEquipmentId.let { id ->
                            equipment.find { it.equipment_id == id }?.let {
                                "${it.identifier} (${it.type})"
                            } ?: "Select Equipment"
                        },
                        onValueChange = {},
                        label = { Text("Equipment*") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = equipmentExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = equipmentExpanded,
                        onDismissRequest = { equipmentExpanded = false }
                    ) {
                        equipment.forEach { equipmentItem ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = equipmentItem.identifier,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = equipmentItem.type,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    selectedEquipmentId = equipmentItem.equipment_id.toString()
                                    equipmentExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description*") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Urgency dropdown
                ExposedDropdownMenuBox(
                    expanded = urgencyExpanded,
                    onExpandedChange = { urgencyExpanded = !urgencyExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = urgency.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("Urgency Level") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = urgencyExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = urgencyExpanded,
                        onDismissRequest = { urgencyExpanded = false }
                    ) {
                        urgencyLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    urgency = level
                                    urgencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedEquipmentId.isNotBlank() && description.isNotBlank()) {
                        val newBreakdown = Breakdown(
                            breakdown_id = null, // handled by supabase
                            equipment_id = selectedEquipmentId,
                            urgency_level = urgency,
                            location = location.ifEmpty { null },
                            description = description,
                            status = "open"
                        )
                        onCreate(newBreakdown)
                    }
                },
                enabled = selectedEquipmentId.isNotBlank() && description.isNotBlank()
            ) {
                Text("Report")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}