package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.viewModels.MyBreakdownsViewModel
import android.R as AndroidR

@Composable
fun MyBreakdownsScreen(
    onBreakdownClick: (String) -> Unit,
    commonActions: CommonScreenActions,
    viewModel: MyBreakdownsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val breakdowns by viewModel.myBreakdowns.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        Log.d("MyBreakdownsScreen", "Loading user's breakdowns")
        viewModel.loadMyBreakdowns()
    }

    if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    WeFixItAppScaffold(
        title = stringResource(R.string.my_breakdowns),
        currentRoute = "my_breakdowns",
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
        authViewModel = authViewModel,
        showBackButton = true,
        onBackClick = commonActions.onBackClick,
        notificationViewModel = hiltViewModel(),
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                    contentDescription = "Filter"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5C5CFF))
                }
            } else if (breakdowns.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No breakdowns reported by you")
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Your Breakdowns",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total: ${breakdowns.size}",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open: ${breakdowns.count { it.status == "open" }}",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "In Progress: ${breakdowns.count { it.status == "in_progress" }}",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Resolved: ${breakdowns.count { it.status == "resolved" }}",
                            fontSize = 14.sp
                        )
                    }
                }

                Text(
                    text = "Your Recent Breakdowns",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(breakdowns.take(5)) { breakdown ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = breakdown.equipment_id ?: "Breakdown",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(text = breakdown.description.take(50) + "...")
                                        Text(
                                            text = "Status: ${breakdown.status}",
                                            color = when (breakdown.status?.lowercase()) {
                                                "open" -> Color.Red
                                                "in_progress" -> Color(0xFFFFA500) // Orange
                                                "resolved" -> Color.Green
                                                else -> Color.Gray
                                            }
                                        )
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Breakdown",
                                        tint = when (breakdown.status?.lowercase()) {
                                            "open" -> Color.Red
                                            "in_progress" -> Color(0xFFFFA500) // Orange
                                            "resolved" -> Color.Green
                                            else -> Color.Gray
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .clickable {
                                        breakdown.breakdown_id?.let { onBreakdownClick(it) }
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                            if (breakdown != breakdowns.take(5).last()) {
                                Divider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (breakdowns.size > 5) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View All (${breakdowns.size})")
                    }
                }
            }
        }
    }
}