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
    // For the bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // For the drawer menu
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val breakdowns by viewModel.breakdowns.collectAsState()

    // Drawer layout for the menu
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

                    MenuItemRow("Home", AndroidR.drawable.ic_menu_agenda) {
                        scope.launch { drawerState.close() }
                        // Already on home, no navigation needed
                    }
                    MenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces) {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                    MenuItemRow("My breakdowns", AndroidR.drawable.ic_menu_report_image) {
                        scope.launch { drawerState.close() }
                        // Navigate to my breakdowns
                    }
                    MenuItemRow("Notifications", AndroidR.drawable.ic_popup_reminder) {
                        scope.launch { drawerState.close() }
                        onNavigateToNotifications()
                    }
                    MenuItemRow("History", AndroidR.drawable.ic_menu_recent_history) {
                        scope.launch { drawerState.close() }
                        // Navigate to history
                    }
                    MenuItemRow("Equipment", AndroidR.drawable.ic_menu_manage) {
                        scope.launch { drawerState.close() }
                        // Navigate to equipment
                    }
                    MenuItemRow("My Assignments", AndroidR.drawable.ic_menu_add) {
                        scope.launch { drawerState.close() }
                        onNavigateToAssignments()
                    }
                    MenuItemRow("Technical Reports", AndroidR.drawable.ic_menu_edit) {
                        scope.launch { drawerState.close() }
                        // Navigate to technical reports
                    }
                    MenuItemRow("Parts & Materials", AndroidR.drawable.ic_menu_compass) {
                        scope.launch { drawerState.close() }
                        // Navigate to parts & materials
                    }
                    MenuItemRow("Calendar", AndroidR.drawable.ic_menu_month) {
                        scope.launch { drawerState.close() }
                        // Navigate to calendar
                    }
                    MenuItemRow("Settings", AndroidR.drawable.ic_menu_preferences) {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    }
                    MenuItemRow("Help/Support", AndroidR.drawable.ic_menu_help) {
                        scope.launch { drawerState.close() }
                        // Navigate to help/support
                    }
                    MenuItemRow("Logout", AndroidR.drawable.ic_menu_close_clear_cancel) {
                        scope.launch { drawerState.close() }
                        // Logout action
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

                // Swipe-up gesture area with sheet handle indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        // Implementação do gesto de deslizar para cima para abrir bottom sheet
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                // Se o gesto for para cima com uma distância significativa
                                if (dragAmount.y < -10) {
                                    showBottomSheet = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Mantém o indicador visual do bottom sheet
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray)
                            .clickable { showBottomSheet = true } // Também permite o clique direto
                    )
                }
            }
        }
    }

    // Bottom sheet com apenas UM indicador visual no topo
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            dragHandle = {
                // Este é o único indicador visual no bottom sheet
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

@Composable
fun MenuItemRow(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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

            val priorityColor = when (breakdown.priority) {
                1 -> Color.Yellow
                2 -> Color.Red
                else -> Color.Green
            }

            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
                    .background(priorityColor)
            )
        }
    }
}