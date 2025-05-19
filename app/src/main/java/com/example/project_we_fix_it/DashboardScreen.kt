package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenChat: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val breakdowns by viewModel.breakdowns.collectAsState()

    if (showMenu) {
        TechnicianMenu(
            isVisible = showMenu,
            onDismiss = { showMenu = false },
            onMenuItemSelected = { item ->
                showMenu = false
                when (item) {
                    "Report a breakdown" -> onNavigateToBreakdownReporting()
                    "View Assignments" -> onNavigateToAssignments()
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(
                            painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            painter = painterResource(id = AndroidR.drawable.ic_menu_preferences),
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Black,
                tonalElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = AndroidR.drawable.ic_menu_myplaces),
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = onNavigateToProfile
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = AndroidR.drawable.ic_menu_agenda),
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { /* Already on home */ }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = AndroidR.drawable.ic_popup_reminder),
                                contentDescription = "Notifications"
                            )
                        },
                        label = { Text("Notifications") },
                        selected = false,
                        onClick = onNavigateToNotifications
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenChat,
                containerColor = Color.White,
                contentColor = Color.DarkGray,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Icon(
                    painter = painterResource(id = AndroidR.drawable.ic_dialog_email),
                    contentDescription = "Chat"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Active Breakdowns",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(breakdowns) { breakdown ->
                    BreakdownCard(
                        breakdown = breakdown,
                        onClick = {
                            navController.navigate("breakdown/${breakdown.id}")
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray)
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate("reportBreakdown")
                        showBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Report a breakdown")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("assignments")
                        showBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View assignments")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BreakdownCard(
    breakdown: BreakdownItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeFixItGrey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = breakdown.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breakdown.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TechnicianMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onMenuItemSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MenuItem("Home", AndroidR.drawable.ic_menu_agenda, onMenuItemSelected)
            MenuItem("Profile", AndroidR.drawable.ic_menu_myplaces, onMenuItemSelected)
            MenuItem("My breakdowns", AndroidR.drawable.ic_menu_report_image, onMenuItemSelected)
            MenuItem("Notifications", AndroidR.drawable.ic_popup_reminder, onMenuItemSelected)
            MenuItem("History", AndroidR.drawable.ic_menu_recent_history, onMenuItemSelected)
            MenuItem("Equipment", AndroidR.drawable.ic_menu_manage, onMenuItemSelected)
            /*MenuItem("My Assignments", AndroidR.drawable.ic_menu_assign, onMenuItemSelected)*/
            MenuItem("Technical Reports", AndroidR.drawable.ic_menu_edit, onMenuItemSelected)
            MenuItem("Parts & Materials", AndroidR.drawable.ic_menu_compass, onMenuItemSelected)
            MenuItem("Calendar", AndroidR.drawable.ic_menu_month, onMenuItemSelected)
            MenuItem("Settings", AndroidR.drawable.ic_menu_preferences, onMenuItemSelected)
            MenuItem("Help/Support", AndroidR.drawable.ic_menu_help, onMenuItemSelected)
            MenuItem("Logout", AndroidR.drawable.ic_menu_close_clear_cancel, onMenuItemSelected)
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    iconRes: Int,
    onMenuItemSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMenuItemSelected(title) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



