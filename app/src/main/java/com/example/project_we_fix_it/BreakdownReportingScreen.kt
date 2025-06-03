package com.example.project_we_fix_it

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownReportingScreen(
    commonActions: CommonScreenActions,
    onSave: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // State variables for form fields
    var equipmentId by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Normal") }
    var selectedDate by remember { mutableStateOf("Select Date") }
    var termsAccepted by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

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
        onLogout = commonActions.logout,
        authViewModel = authViewModel,
        showBackButton = true,
        onBackClick = commonActions.onBackClick
    ) { padding ->
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

            OutlinedTextField(
                value = equipmentId,
                onValueChange = { equipmentId = it },
                label = { Text("Equipment Identification") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO: Add image */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Insert Image")
                }

                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(urgency)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Low") },
                            onClick = {
                                urgency = "Low"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Normal") },
                            onClick = {
                                urgency = "Normal"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("High") },
                            onClick = {
                                urgency = "High"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Critical") },
                            onClick = {
                                urgency = "Critical"
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedDate)
            }

            if (showDatePicker) {
                // TODO: Implement date picker dialog
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text("I accept the terms")
                TextButton(onClick = { /* TODO: Show T&Cs */ }) {
                    Text("Read our T&Cs")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = termsAccepted && equipmentId.isNotBlank()
                        && location.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save Breakdown")
            }
        }
    }
}