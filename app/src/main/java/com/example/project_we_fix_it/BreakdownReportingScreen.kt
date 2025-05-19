package com.example.project_we_fix_it

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownReportingScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Breakdown") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = onNavigateToProfile
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = onNavigateToHome
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, "Notifications") },
                        label = { Text("Notifications") },
                        selected = false,
                        onClick = onNavigateToNotifications
                    )
                }
            }
        }
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
                value = "",
                onValueChange = {},
                label = { Text("Equipment Identification") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
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
                var urgency by remember { mutableStateOf("Normal") }

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

            var showDatePicker by remember { mutableStateOf(false) }
            var selectedDate by remember { mutableStateOf("Select Date") }

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
                    checked = false,
                    onCheckedChange = { /* TODO */ }
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
                enabled = true // TODO: Add validation
            ) {
                Text("Save Breakdown")
            }
        }
    }
}