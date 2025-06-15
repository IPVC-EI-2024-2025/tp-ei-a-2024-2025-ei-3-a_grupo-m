package com.example.project_we_fix_it.adminViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.R
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.viewModels.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    commonActions: CommonScreenActions,
    viewModel: AdminViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    val users by viewModel.users.collectAsStateWithLifecycle()
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()
    val breakdowns by viewModel.breakdowns.collectAsStateWithLifecycle()

    WeFixItAppScaffold(
        title = "Admin Dashboard",
        currentRoute = "admin/dashboard",
        navController = commonActions.navController,
        onNavigateToProfile = commonActions.navigateToProfile,
        onNavigateToHome = commonActions.navigateToHome,
        onOpenSettings = commonActions.openSettings,
        onNavigateToNotifications = commonActions.navigateToNotifications,
        onNavigateToAssignments = commonActions.navigateToAssignments,
        onNavigateToBreakdownReporting = commonActions.navigateToBreakdownReporting,
        onNavigateToMessages = commonActions.navigateToMessages,
        onLogout = commonActions.logout,
        showBackButton = false,
        onNavigateToAdminDashboard = commonActions.navigateToAdminDashboard,
        onNavigateToAdminUsers = commonActions.navigateToAdminUsers,
        onNavigateToAdminEquipment = commonActions.navigateToAdminEquipment,
        onNavigateToAdminBreakdowns = commonActions.navigateToAdminBreakdowns,
        onNavigateToAdminAssignments = commonActions.navigateToAdminAssignments,
        notificationViewModel = hiltViewModel()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardCard(
                    icon = Icons.Default.People,
                    title = stringResource(R.string.users),
                    count = users.size,
                    onClick = { commonActions.navController.navigate("admin/users") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                DashboardCard(
                    icon = Icons.Default.Build,
                    title = stringResource(R.string.equipment),
                    count = equipment.size,
                    onClick = { commonActions.navController.navigate("admin/equipment") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardCard(
                    icon = Icons.Default.Warning,
                    title = stringResource(R.string.breakdowns),
                    count = breakdowns.size,
                    onClick = { commonActions.navController.navigate("admin/breakdowns") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                DashboardCard(
                    icon = Icons.Default.Task,
                    title = stringResource(R.string.assignments),
                    count = 0, // You might want to add assignments count to your view model
                    onClick = { commonActions.navController.navigate("admin/assignments") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Breakdowns
            Text(
                text = stringResource(R.string.recent_breakdowns),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (breakdowns.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_recent_breakdowns))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(breakdowns.take(5)) { breakdown ->
                        ListItem(
                            headlineContent = { Text(breakdown.description) },
                            supportingContent = { Text("Status: ${breakdown.status}") },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = stringResource(R.string.breakdowns)
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    icon: ImageVector,
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}