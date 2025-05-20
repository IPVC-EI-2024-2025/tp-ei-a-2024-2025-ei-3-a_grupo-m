package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var isDarkThemeEnabled by remember { mutableStateOf(false) }

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
                        onNavigateToHome()
                    }
                    MenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces) {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                    MenuItemRow("Settings", AndroidR.drawable.ic_menu_preferences) {
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
                            text = "Settings",
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
                        IconButton(onClick = {  }) {
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
                            selected = false,
                            onClick = onNavigateToHome
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
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SettingsItem(
                        title = "Language",
                        description = "Choose your language",
                        showArrow = true,
                        onClick = { /* Open language selection */ }
                    )

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    // Notifications Settings
                    SettingsItem(
                        title = "Notifications",
                        description = "Notifications preferences",
                        showArrow = true,
                        onClick = { /* Open notification settings */ }
                    )

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    SettingsItem(
                        title = "Account Settings",
                        description = "Manage your account settings",
                        showArrow = true,
                        onClick = { /* Open account settings */ }
                    )

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Dark Theme",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Change to dark theme",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = isDarkThemeEnabled,
                            onCheckedChange = { isDarkThemeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF5C5CFF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = AndroidR.drawable.ic_dialog_info),
                            contentDescription = "About",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "About",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        if (showArrow) {
            Icon(
                painter = painterResource(id = AndroidR.drawable.ic_media_play),
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}