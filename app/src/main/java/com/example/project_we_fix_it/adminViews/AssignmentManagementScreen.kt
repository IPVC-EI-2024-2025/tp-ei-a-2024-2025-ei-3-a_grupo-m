package com.example.project_we_fix_it.adminViews

import AdminScaffold
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentManagementScreen(
    commonActions: CommonScreenActions,
    viewModel: AdminViewModel = hiltViewModel()
) {
    Log.d("AssignmentScreen", "Composable started")

    var showAddDialog by remember { mutableStateOf(false) }
    var editAssignment by remember { mutableStateOf<Assignment?>(null) }
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("AssignmentScreen", "Loading assignments")
        viewModel.loadAllData()
    }

    AdminScaffold(
        title = "Assignment Management",
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
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (assignments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No assignments found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(assignments) { assignment ->
                        AssignmentItem(
                            assignment = assignment,
                            onEdit = { editAssignment = assignment },
                            onDelete = { assignment.assignment_id?.let { viewModel.deleteAssignment(it) } },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog || editAssignment != null) {
        AssignmentEditDialog(
            assignment = editAssignment,
            viewModel = viewModel,
            onDismiss = {
                showAddDialog = false
                editAssignment = null
            },
            onSave = { assignment ->
                if (assignment.assignment_id == null) {
                    viewModel.createAssignment(assignment)
                } else {
                    viewModel.updateAssignment(assignment.assignment_id, assignment.status)
                }
                showAddDialog = false
                editAssignment = null
            }
        )
    }
}

@Composable
fun AssignmentItem(
    assignment: Assignment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    viewModel: AdminViewModel
) {
    val breakdowns by viewModel.breakdowns.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()

    val breakdown = breakdowns.find { it.breakdown_id == assignment.breakdown_id }
    val technician = users.find { it.user_id == assignment.technician_id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Assignment ID: ${assignment.assignment_id ?: "New"}",
                    fontWeight = FontWeight.Bold
                )
                Text("Breakdown: ${breakdown?.description?.take(30) ?: "None"}")
                Text("Technician: ${technician?.name ?: "None"}")
                Text("Status: ${assignment.status.replaceFirstChar { it.uppercase() }}")
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentEditDialog(
    assignment: Assignment?,
    viewModel: AdminViewModel,
    onDismiss: () -> Unit,
    onSave: (Assignment) -> Unit
) {
    var breakdownId by remember { mutableStateOf(assignment?.breakdown_id ?: "") }
    var technicianId by remember { mutableStateOf(assignment?.technician_id ?: "") }
    var status by remember { mutableStateOf(assignment?.status ?: "active") }

    val breakdowns by viewModel.breakdowns.collectAsStateWithLifecycle()
    val technicians by viewModel.users.collectAsStateWithLifecycle()

    val filteredTechnicians = remember(technicians) {
        technicians.filter { it.role == "technician" }
    }

    val isTechnicianValid = remember(technicianId) {
        filteredTechnicians.any { it.user_id == technicianId }
    }


    var breakdownExpanded by remember { mutableStateOf(false) }
    var technicianExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (assignment == null) "Add Assignment" else "Edit Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = breakdownExpanded,
                    onExpandedChange = { breakdownExpanded = !breakdownExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = breakdownId.let { id ->
                            breakdowns.find { it.breakdown_id == id }?.let {
                                "Breakdown #${it.breakdown_id} (${it.status})"
                            } ?: "Select Breakdown"
                        },
                        onValueChange = {},
                        label = { Text("Breakdown") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = breakdownExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = breakdownExpanded,
                        onDismissRequest = { breakdownExpanded = false }
                    ) {
                        breakdowns.forEach { breakdown ->
                            DropdownMenuItem(
                                text = {
                                    Text("Breakdown #${breakdown.breakdown_id} (${breakdown.status})")
                                },
                                onClick = {
                                    breakdownId = breakdown.breakdown_id.toString()
                                    breakdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Technician dropdown
                ExposedDropdownMenuBox(
                    expanded = technicianExpanded,
                    onExpandedChange = { technicianExpanded = !technicianExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = technicianId.let { id ->
                            filteredTechnicians.find { it.user_id == id }?.name ?: "Select Technician"
                        },
                        onValueChange = {},
                        label = { Text("Technician") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = technicianExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = technicianExpanded,
                        onDismissRequest = { technicianExpanded = false }
                    ) {
                        filteredTechnicians.forEach { technician ->
                            DropdownMenuItem(
                                text = { Text(technician.name) },
                                onClick = {
                                    technicianId = technician.user_id
                                    technicianExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = status,
                        onValueChange = {},
                        label = { Text("Status") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        listOf("active", "inactive").forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    status = selectionOption
                                    statusExpanded = false
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
                    if (isTechnicianValid) {
                        onSave(
                            Assignment(
                                assignment_id = assignment?.assignment_id,
                                breakdown_id = breakdownId,
                                technician_id = technicianId,
                                status = status
                            )
                        )
                    }
                },
                enabled = breakdownId.isNotBlank() && technicianId.isNotBlank() && isTechnicianValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}