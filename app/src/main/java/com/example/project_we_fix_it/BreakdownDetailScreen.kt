package com.example.project_we_fix_it

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownDetailsScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    WeFixItAppScaffold(
        title = "Breakdown Details",
        currentRoute = "breakdown_details",
        navController = navController,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onOpenSettings = onOpenSettings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAssignments = onNavigateToAssignments,
        onNavigateToBreakdownReporting = onNavigateToBreakdownReporting,
        onLogout = onLogout,
        authViewModel = hiltViewModel()
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

                DetailRow("Assigned to", "Technician Joaquim Morala")

                DetailRow("Equipment Identification", "Computer 1233123423")

                DetailRow("Description", "The computer exploded")

                DetailRow("Date", "15/04/25")

                DetailRow("Location", "4° Block")

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Add action */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Critical")
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
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