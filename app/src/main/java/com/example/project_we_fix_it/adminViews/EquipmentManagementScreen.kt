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
import com.example.project_we_fix_it.supabase.Equipment
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentManagementScreen(
    commonActions: CommonScreenActions,
    viewModel: AdminViewModel = hiltViewModel()
) {
    Log.d("EquipmentScreen", "Composable started")

    var showAddDialog by remember { mutableStateOf(false) }
    var editEquipment by remember { mutableStateOf<Equipment?>(null) }
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()
    LaunchedEffect(equipment) {
        Log.d("EquipmentScreen", "Equipment list updated: ${equipment.size} items")
    }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("EquipmentScreen", "LaunchedEffect started")
        viewModel.loadAllData()
    }

    AdminScaffold(
        title = "Equipment Management",
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
                Icon(Icons.Default.Add, contentDescription = "Add Equipment")
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

            if (equipment.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No equipment found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(equipment) { equipment ->
                        EquipmentItem(
                            equipment = equipment,
                            onEdit = { editEquipment = equipment },
                            onDelete = { equipment.equipment_id?.let { viewModel.deleteEquipment(it) } }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog || editEquipment != null) {
        EquipmentEditDialog(
            equipment = editEquipment,
            onDismiss = {
                showAddDialog = false
                editEquipment = null
            },
            onSave = { equipment ->
                Log.d("EquipmentScreen", "Saving equipment: $equipment with ID: ${equipment.equipment_id}")
                if (equipment.equipment_id == null) {
                    viewModel.createEquipment(equipment)
                } else {
                    viewModel.updateEquipment(equipment)
                }
                showAddDialog = false
                editEquipment = null
            }
        )
    }
}

@Composable
fun EquipmentItem(
    equipment: Equipment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    text = equipment.identifier,
                    fontWeight = FontWeight.Bold
                )
                Text("Type: ${equipment.type}")
                equipment.model?.let { Text("Model: $it") }
                equipment.location?.let { Text("Location: $it") }
                Text("Status: ${equipment.status}")
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
fun EquipmentEditDialog(
    equipment: Equipment?,
    onDismiss: () -> Unit,
    onSave: (Equipment) -> Unit
) {
    var identifier by remember { mutableStateOf(equipment?.identifier ?: "") }
    var type by remember { mutableStateOf(equipment?.type ?: "") }
    var model by remember { mutableStateOf(equipment?.model ?: "") }
    var location by remember { mutableStateOf(equipment?.location ?: "") }
    var status by remember { mutableStateOf(equipment?.status ?: "active") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipment == null) "Add Equipment" else "Edit Equipment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("Identifier*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        value = status,
                        onValueChange = {},
                        label = { Text("Status") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("active", "inactive", "unavailable").forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    status = selectionOption
                                    expanded = false
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
                    onSave(
                        Equipment(
                            equipment_id = equipment?.equipment_id,
                            identifier = identifier,
                            type = type,
                            model = model.ifEmpty { null },
                            location = location.ifEmpty { null },
                            status = status
                        )
                    )
                },
                enabled = identifier.isNotBlank() && type.isNotBlank()
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