package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_we_fix_it.composables.BreakdownCard
import com.example.project_we_fix_it.composables.DashboardMenuItemRow
import kotlinx.coroutines.launch
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
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val breakdowns by viewModel.breakdowns.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Menu - Technicians",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    DashboardMenuItemRow("Home", AndroidR.drawable.ic_menu_agenda) {
                        scope.launch { drawerState.close() }
                    }
                    DashboardMenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces) {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                    DashboardMenuItemRow("My breakdowns", AndroidR.drawable.ic_menu_report_image) {
                        scope.launch { drawerState.close() }
                        navController.navigate("my_breakdowns")
                    }
                    DashboardMenuItemRow("Notifications", AndroidR.drawable.ic_popup_reminder) {
                        scope.launch { drawerState.close() }
                        onNavigateToNotifications()
                    }
                    DashboardMenuItemRow("History", AndroidR.drawable.ic_menu_recent_history) {
                        scope.launch { drawerState.close() }
                    }
                    DashboardMenuItemRow("My Assignments", AndroidR.drawable.ic_menu_add) {
                        scope.launch { drawerState.close() }
                        onNavigateToAssignments()
                    }
                    DashboardMenuItemRow("Calendar", AndroidR.drawable.ic_menu_month) {
                        scope.launch { drawerState.close() }
                    }
                    DashboardMenuItemRow("Settings", AndroidR.drawable.ic_menu_preferences) {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    }
                    DashboardMenuItemRow("Help/Support", AndroidR.drawable.ic_menu_help) {
                        scope.launch { drawerState.close() }
                    }
                }
            }
        }
    ) {
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
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
                    .padding(horizontal = 16.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)

                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()

                                if (dragAmount.y < -10) {
                                    showBottomSheet = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray)
                            .clickable { showBottomSheet = true }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        onNavigateToBreakdownReporting()
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C5CFF) // Purple color as shown in image
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Report a breakdown",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onNavigateToAssignments()
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C5CFF) // Purple color as shown in image
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "View Assignments",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}



