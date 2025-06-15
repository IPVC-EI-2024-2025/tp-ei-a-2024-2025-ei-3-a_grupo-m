package com.example.project_we_fix_it

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.viewModels.BreakdownReportingViewModel
import android.graphics.BitmapFactory
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownReportingScreen(
    commonActions: CommonScreenActions,
    onSave: (breakdownId: String) -> Unit,
    viewModel: BreakdownReportingViewModel = hiltViewModel()
) {
    var breakdownName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Normal") }
    var termsAccepted by remember { mutableStateOf(false) }
    var showUrgencyDropdown by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val photosToUpload by viewModel.photosToUpload.collectAsState()
    val urgencyLevels = listOf("Low", "Normal", "High", "Critical")
    val context = LocalContext.current

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()
                    viewModel.addPhotoToUpload(imageBytes)
                }
            }
        }
    )

    WeFixItAppScaffold(
        title = "Report Breakdown",
        currentRoute = "breakdown_reporting",
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = stringResource(R.string.breakdown_info),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Photo upload section
                Text(
                    text = "Photos (Optional)",
                    style = MaterialTheme.typography.titleMedium
                )

                // Selected photos preview
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photosToUpload.size) { index ->
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            val bitmap = remember(photosToUpload[index]) {
                                BitmapFactory.decodeByteArray(photosToUpload[index], 0, photosToUpload[index].size)
                            }
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Selected photo $index",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removePhotoToUpload(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Photo")
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(R.string.problem_description))},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    supportingText = { Text(text = stringResource(R.string.describe_problem))}
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(text = stringResource(R.string.location))},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showUrgencyDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(urgency)
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.select_urgency)
                        )
                    }
                    DropdownMenu(
                        expanded = showUrgencyDropdown,
                        onDismissRequest = { showUrgencyDropdown = false }
                    ) {
                        urgencyLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    urgency = level
                                    showUrgencyDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it }
                    )
                    Text(text = stringResource(R.string.confirm_information))
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.reportBreakdown(
                            description = description,
                            location = location,
                            urgencyLevel = urgency,
                            onSuccess = onSave
                        )
                    },
                    enabled = termsAccepted && description.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.report_breakdown))
                }
            }
        }
    }
}