package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.*
import com.example.project_we_fix_it.nav.AppNavigator
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.nav.Routes
import com.example.project_we_fix_it.viewModels.AssignmentViewModel

@Composable
fun WorkingOnBreakdowns(
    breakdowns: List<BreakdownItem>,
    onBreakdownClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(breakdowns) { breakdown ->
            WorkingOnBreakdownItem(
                breakdown = breakdown,
                onBreakdownClick = onBreakdownClick
            )
        }
    }
}

@Composable
fun WorkingOnBreakdownItem(
    breakdown: BreakdownItem,
    onBreakdownClick: (String) -> Unit
) {
    BreakdownCard(
        breakdown = breakdown,
        onClick = { onBreakdownClick(breakdown.id) },
        trailingContent = {

        }
    )
}

@Composable
fun AssignedBreakdowns(
    breakdowns: List<BreakdownItem>,
    onBreakdownClick: (String) -> Unit,
    activelyWorkingOn: Set<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(breakdowns) { breakdown ->
            AssignedBreakdownItem(
                breakdown = breakdown,
                onBreakdownClick = onBreakdownClick,
                isWorkingOn = activelyWorkingOn.contains(breakdown.id)
            )
        }
    }
}

@Composable
fun AssignedBreakdownItem(
    breakdown: BreakdownItem,
    onBreakdownClick: (String) -> Unit,
    isWorkingOn: Boolean
) {
    BreakdownCard(
        breakdown = breakdown,
        onClick = { onBreakdownClick(breakdown.id) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                if (isWorkingOn) {
                    Text(
                        text = stringResource(R.string.working_on),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.pending),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    )
}

@Composable
fun PriorityIndicator(priority: Int) {
    val priorityColor = when (priority) {
        1 -> Color.Gray
        2 -> Color.Yellow
        else -> Color.Red
    }

    Box(
        modifier = Modifier
            .size(16.dp)
            .background(priorityColor)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAssignmentsScreen(
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel(),
    onBreakdownClick: (String) -> Unit,
    assignmentViewModel: AssignmentViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Assigned Breakdowns", "Working On")

    val authState by authViewModel.authState.collectAsState()
    val assignments by assignmentViewModel.assignments.collectAsState()
    val workingOnBreakdowns by assignmentViewModel.workingOnBreakdowns.collectAsState()
    val assignedBreakdowns by assignmentViewModel.assignedBreakdowns.collectAsState()
    val isLoading by assignmentViewModel.isLoading.collectAsState()
    val errorMessage by assignmentViewModel.errorMessage.collectAsState()
    val currentUserId = authState.user?.id

    var showStartWorkingDialog by remember { mutableStateOf<String?>(null) }
    var showCompleteRequestDialog by remember { mutableStateOf<String?>(null) }

    val activelyWorkingOn by assignmentViewModel.activelyWorkingOn.collectAsState()

    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            Log.d("AssignmentsScreen", "Initial load of assignments")
            assignmentViewModel.loadAssignments(userId)
        }
    }

    val navController = commonActions.navController
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)

    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == Routes.ASSIGNMENTS) {
            currentUserId?.let { userId ->
                Log.d("AssignmentsScreen", "Reloading assignments on return")
                assignmentViewModel.refreshAssignments(userId)
            }
        }
    }

    showStartWorkingDialog?.let { breakdownId ->
        AlertDialog(
            onDismissRequest = { showStartWorkingDialog = null },
            title = { Text(stringResource(R.string.start_working)) },
            text = { Text(stringResource(R.string.move_to_working)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentUserId?.let { userId ->
                            assignmentViewModel.startWorkingOnBreakdown(breakdownId, userId)
                        }
                        showStartWorkingDialog = null
                    }
                ) {
                    Text(stringResource(R.string.start_working_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartWorkingDialog = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    showCompleteRequestDialog?.let { breakdownId ->
        AlertDialog(
            onDismissRequest = { showCompleteRequestDialog = null },
            title = { Text(stringResource(R.string.request_complete)) },
            text = { Text(stringResource(R.string.request_complete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentUserId?.let { userId ->
                            assignmentViewModel.requestCompleteBreakdown(breakdownId, userId)
                        }
                        showCompleteRequestDialog = null
                    }
                ) {
                    Text(stringResource(R.string.request_completion))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCompleteRequestDialog = null }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val workingOnBreakdownItems by remember {
        derivedStateOf {
            assignmentViewModel.workingOnBreakdowns.value
                .filter { breakdown -> breakdown.status != "completed" }
                .map { breakdown ->
                    BreakdownItem(
                        id = breakdown.breakdown_id ?: "",
                        title = breakdown.description.take(30),
                        description = breakdown.description,
                        priority = when (breakdown.urgency_level) {
                            "critical" -> 3
                            "high" -> 2
                            else -> 1
                        }
                    )
                }
        }
    }

    val assignedBreakdownItems = remember(assignmentViewModel.assignedBreakdowns) {
        assignmentViewModel.assignedBreakdowns.value
            .filter { breakdown -> breakdown.status != "completed" }
            .map { breakdown ->
                BreakdownItem(
                    id = breakdown.breakdown_id ?: "",
                    title = breakdown.description.take(30),
                    description = breakdown.description,
                    priority = when (breakdown.urgency_level) {
                        "critical" -> 3
                        "high" -> 2
                        else -> 1
                    }
                )
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessage.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    WeFixItAppScaffold(
        title = stringResource(R.string.assignments),
        currentRoute = "assignments",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    if (assignedBreakdownItems.isEmpty()) {
                    } else {
                        AssignedBreakdowns(
                            assignedBreakdownItems,
                            onBreakdownClick = { id ->
                                if (activelyWorkingOn.contains(id)) {
                                } else {
                                    showStartWorkingDialog = id
                                }
                            },
                            activelyWorkingOn = activelyWorkingOn
                        )
                    }
                }
                1 -> {
                    if (workingOnBreakdownItems.isEmpty()) {
                    } else {
                        WorkingOnBreakdowns(
                            workingOnBreakdownItems,
                            onBreakdownClick = { id ->
                                showCompleteRequestDialog = id
                            }
                        )
                    }
                }
            }
        }
    }
}