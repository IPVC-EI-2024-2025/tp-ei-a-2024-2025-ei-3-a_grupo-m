package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.composables.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAssignmentsScreen(
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onBreakdownClick: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onNavigateToAssignments: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Working On", "Assigned Breakdowns")

    // Sample data (would normally come from ViewModel)
    val workingOnBreakdowns = remember { generateSampleBreakdowns(5, "Working") }
    val assignedBreakdowns = remember { generateSampleBreakdowns(7, "Assigned") }



    WeFixItAppScaffold(
        title = "Assignments",
        currentRoute = "assignments",
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
                // Tabs
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

                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> WorkingOnBreakdowns(workingOnBreakdowns, onBreakdownClick)
                    1 -> AssignedBreakdowns(assignedBreakdowns, onBreakdownClick)
                }
            }
        }
    }


// Helper function for sample data
private fun generateSampleBreakdowns(count: Int, prefix: String): List<BreakdownItem> {
    return List(count) { index ->
        BreakdownItem(
            id = "${index + 1}",
            title = "$prefix Breakdown ${index + 1}",
            description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
            priority = when (index % 3) {
                0 -> 1
                1 -> 2
                else -> 3
            }
        )
    }
}

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
            WorkingOnBreakdownItem(breakdown = breakdown, onBreakdownClick = onBreakdownClick)
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
            Checkbox(
                checked = true,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    )
}

@Composable
fun AssignedBreakdowns(
    breakdowns: List<BreakdownItem>,
    onBreakdownClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(breakdowns) { breakdown ->
            AssignedBreakdownItem(breakdown = breakdown, onBreakdownClick = onBreakdownClick)
        }
    }
}

@Composable
fun AssignedBreakdownItem(
    breakdown: BreakdownItem,
    onBreakdownClick: (String) -> Unit
) {
    BreakdownCard(
        breakdown = breakdown,
        onClick = { onBreakdownClick(breakdown.id) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                PriorityIndicator(priority = breakdown.priority)
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