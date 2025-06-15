package com.example.project_we_fix_it

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.composables.BreakdownCard
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.Equipment
import com.example.project_we_fix_it.viewModels.DashboardViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    commonActions: CommonScreenActions,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val breakdowns by viewModel.breakdowns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadBreakdowns()
    }

    WeFixItAppScaffold(
        title = stringResource(R.string.home),
        currentRoute = "home",
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
        authViewModel = hiltViewModel()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 72.dp)
            ) {
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.active_breakdowns),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (breakdowns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.no_active_breakdowns))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(breakdowns) { breakdown ->
                            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                                BreakdownCard(
                                    breakdown = BreakdownItem(
                                        id = breakdown.breakdown_id ?: "",
                                        title = breakdown.description.take(30),
                                        description = breakdown.description,
                                        priority = when (breakdown.urgency_level) {
                                            "critical" -> 3
                                            "high" -> 2
                                            else -> 1
                                        }
                                    ),
                                    onClick = {
                                        breakdown.breakdown_id?.let { id ->
                                            commonActions.navigateToBreakdownDetails(id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Breakdown"
                )
            }
        }
    }

    if (showCreateDialog) {
        SharedBreakdownCreateDialog(
            equipment = viewModel.equipment.collectAsState().value,
            onDismiss = { showCreateDialog = false },
            onCreate = { breakdown, photos ->
                viewModel.createBreakdown(breakdown, photos)
                showCreateDialog = false
            },
            showError = viewModel.error.collectAsState().value?.let {
                it.contains("Failed to create breakdown")
            } ?: false,
            viewModel = viewModel
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedBreakdownCreateDialog(
    equipment: List<Equipment>,
    onDismiss: () -> Unit,
    onCreate: (Breakdown, List<ByteArray>?) -> Unit,
    showError: Boolean = false,
    viewModel: DashboardViewModel
) {
    var selectedEquipmentId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("low") }
    var location by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var selectedPhotos by remember { mutableStateOf<List<ByteArray>?>(null) }

    var equipmentExpanded by remember { mutableStateOf(false) }
    var urgencyExpanded by remember { mutableStateOf(false) }

    val urgencyLevels = listOf("low", "medium", "high")
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()

                selectedPhotos = (selectedPhotos ?: emptyList()) + listOf(imageBytes)
            } catch (e: Exception) {
                showError = true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report New Breakdown") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showError) {
                    Text(
                        text = "Failed to create breakdown. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (equipment.isEmpty()) {
                    Text("Loading equipment...", color = Color.Gray)
                } else {
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
                                        selectedEquipmentId = equipmentItem.equipment_id ?: ""
                                        equipmentExpanded = false
                                    }
                                )
                            }
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

                Spacer(modifier = Modifier.height(8.dp))
                Text("Add Photos (Optional)", fontWeight = FontWeight.Medium)

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Add Photo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Photo")
                }

                selectedPhotos?.let { photos ->
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(photos) { photoBytes ->
                            Image(
                                bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                                    .asImageBitmap(),
                                contentDescription = "Selected photo",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
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
                            equipment_id = selectedEquipmentId,
                            urgency_level = urgency,
                            location = location.ifEmpty { null },
                            description = description,
                            status = "open"
                        )
                        onCreate(newBreakdown, selectedPhotos)
                        showError = false
                    } else {
                        showError = true
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
