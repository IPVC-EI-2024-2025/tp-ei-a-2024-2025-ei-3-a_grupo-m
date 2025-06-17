package com.example.project_we_fix_it.adminViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
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
import com.example.project_we_fix_it.supabase.BreakdownPhoto
import com.example.project_we_fix_it.supabase.UserProfile
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import java.io.ByteArrayOutputStream
import android.R as AndroidR
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownManagementScreen(
    commonActions: CommonScreenActions,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val breakdowns by viewModel.breakdowns.collectAsState()
    val users by viewModel.users.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val breakdownPhotos by viewModel.breakdownPhotos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filterStatus by remember { mutableStateOf("all") }
    var showTechnicianDialog by remember { mutableStateOf(false) }
    var selectedBreakdown by remember { mutableStateOf<Breakdown?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedBreakdownForPhotos by remember { mutableStateOf<Breakdown?>(null) }

    val filteredBreakdowns = remember(breakdowns, filterStatus) {
        when (filterStatus) {
            "all" -> breakdowns
            else -> breakdowns.filter { it.status == filterStatus }
        }
    }

    val technicians = remember(users) {
        users.filter { it.role == "technician" && it.status == "active" }
    }

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
                                photos = breakdownPhotos[breakdown.breakdown_id] ?: emptyList(),
                                onClick = {
                                    breakdown.breakdown_id?.let {
                                        commonActions.navigateToBreakdownDetails(it)
                                    }
                                },
                                onAssignClick = {
                                    selectedBreakdown = breakdown
                                    showTechnicianDialog = true
                                },
                                onPhotosClick = {
                                    selectedBreakdownForPhotos = breakdown
                                    showPhotoDialog = true
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
                    val updatedBreakdown = breakdown.copy(status = "in_progress")
                    viewModel.updateBreakdown(updatedBreakdown)

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
            viewModel = viewModel,
            breakdown = selectedBreakdown!!
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

    if (showPhotoDialog && selectedBreakdownForPhotos != null) {
        PhotoManagementDialog(
            breakdown = selectedBreakdownForPhotos!!,
            photos = breakdownPhotos[selectedBreakdownForPhotos!!.breakdown_id] ?: emptyList(),
            viewModel = viewModel,
            onDismiss = {
                showPhotoDialog = false
                selectedBreakdownForPhotos = null
            }
        )
    }
}

@Composable
private fun BreakdownCard(
    breakdown: Breakdown,
    photos: List<BreakdownPhoto>,
    onClick: () -> Unit,
    onAssignClick: () -> Unit,
    onPhotosClick: () -> Unit,
    viewModel: AdminViewModel
) {
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()
    val currentEquipment = equipment.find { it.equipment_id == breakdown.equipment_id }
    var expanded by remember { mutableStateOf(false) }

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
                var statusExpanded by remember { mutableStateOf(false) }
                Box {
                    Box(
                        modifier = Modifier
                            .clickable { statusExpanded = true }
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
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        listOf("open", "in_progress", "completed").forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(status.replace("_", " ").replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    viewModel.updateBreakdown(breakdown.copy(status = status))
                                    statusExpanded = false
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Photos button
                    IconButton(
                        onClick = onPhotosClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Badge(
                            containerColor = if (photos.isNotEmpty()) Color(0xFF5C5CFF) else Color.Gray
                        ) {
                            Text(
                                text = photos.size.toString(),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Photos (${photos.size})",
                            tint = if (photos.isNotEmpty()) Color(0xFF5C5CFF) else Color.Gray
                        )
                    }

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

            if (photos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Photos (${photos.size})",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color.Gray
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(photos.take(3)) { photo ->
                            Image(
                                painter = rememberAsyncImagePainter(photo.photo_url),
                                contentDescription = "Breakdown photo",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (photos.size > 3) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Gray.copy(alpha = 0.3f))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${photos.size - 3}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoManagementDialog(
    breakdown: Breakdown,
    photos: List<BreakdownPhoto>,
    viewModel: AdminViewModel,
    onDismiss: () -> Unit
) {
    var showFullImage by remember { mutableStateOf(false) }
    var selectedPhotoUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Image picker launcher
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
                val fileName = "photo_${System.currentTimeMillis()}.jpg"

                breakdown.breakdown_id?.let { breakdownId ->
                    viewModel.uploadBreakdownPhoto(breakdownId, imageBytes, fileName)
                }
            } catch (e: Exception) {
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Photos - ${breakdown.breakdown_id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C5CFF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photos grid
                if (photos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No photos available",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(photos.chunked(2)) { photoRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                photoRow.forEach { photo ->
                                    PhotoItem(
                                        photo = photo,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedPhotoUrl = photo.photo_url
                                            showFullImage = true
                                        },
//                                        onDelete = {
//                                            photo.photo_id?.let { photoId ->
//                                                val filePath = photo.photo_url
//                                                    .substringAfterLast("breakdown_photos/")
//                                                    .substringBefore("?")
//                                                viewModel.deleteBreakdownPhoto(photoId, filePath)
//                                            }
//                                        }
                                    )
                                }
                                if (photoRow.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFullImage) {
        Dialog(
            onDismissRequest = { showFullImage = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedPhotoUrl),
                        contentDescription = "Full size photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    IconButton(
                        onClick = { showFullImage = false },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(
    photo: BreakdownPhoto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
//    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(photo.photo_url),
                contentDescription = "Breakdown photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Delete button
//            IconButton(
//                onClick = onDelete,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .background(
//                        Color.Black.copy(alpha = 0.5f),
//                        CircleShape
//                    )
//                    .size(32.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Delete photo",
//                    tint = Color.White,
//                    modifier = Modifier.size(16.dp)
//                )
//            }

            photo.uploaded_at?.let { uploadedAt ->
                val formattedDate = try {
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(uploadedAt)
                    SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(date!!)
                } catch (e: Exception) {
                    "Unknown date"
                }

                Text(
                    text = formattedDate,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            RoundedCornerShape(topEnd = 8.dp)
                        )
                        .padding(4.dp),
                    fontSize = 10.sp,
                    color = Color.White
                )
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

    var equipmentExpanded by remember { mutableStateOf(false) }
    var urgencyExpanded by remember { mutableStateOf(false) }

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