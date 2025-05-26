package com.example.project_we_fix_it

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAssignmentsScreen(
    navController: NavHostController,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onBreakdownClick: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Working On", "Assigned Breakdowns")

    val workingOnBreakdowns = remember {
        listOf(
            BreakdownItem(
                id = "1",
                title = "Breakdown 2",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "2",
                title = "Breakdown 2",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "3",
                title = "Breakdown 2",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "4",
                title = "Breakdown 2",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "5",
                title = "Breakdown 2",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            )
        )
    }

    val assignedBreakdowns = remember {
        listOf(
            BreakdownItem(
                id = "6",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "7",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 2
            ),
            BreakdownItem(
                id = "8",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 3
            ),
            BreakdownItem(
                id = "9",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 1
            ),
            BreakdownItem(
                id = "10",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 3
            ),
            BreakdownItem(
                id = "11",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 3
            ),
            BreakdownItem(
                id = "12",
                title = "Breakdown 3",
                description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                priority = 3
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Assignments") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_preferences),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
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
                    selected = true,
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onBreakdownClick(breakdown.id) },
        colors = CardDefaults.cardColors(containerColor = WeFixItGrey),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = breakdown.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breakdown.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Checkbox(
                checked = true,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onBreakdownClick(breakdown.id) },
        colors = CardDefaults.cardColors(containerColor = WeFixItGrey),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = breakdown.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breakdown.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val priorityColor = when (breakdown.priority) {
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
            }
        }
    }
}