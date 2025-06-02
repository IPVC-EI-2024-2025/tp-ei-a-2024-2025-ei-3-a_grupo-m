package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.ProfileInfoItem
import com.example.project_we_fix_it.composables.ProfileMenuItemRow
import kotlinx.coroutines.launch
import android.R as AndroidR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            onLogout()
        }
    }
    LaunchedEffect(navController.currentBackStackEntry) {
        Log.d("ProfileScreen", "Reloading profile data")
        authViewModel.loadUserProfile()
    }

    if (authState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF5C5CFF))
        }
        return
    }

    // Get the user profile data
    val userProfile = authState.userProfile
    val user = authState.user

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                    }
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

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
                        text = "Menu - ${userProfile?.role?.replaceFirstChar { it.uppercase() } ?: "User"}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    ProfileMenuItemRow("Home", AndroidR.drawable.ic_menu_agenda) {
                        scope.launch { drawerState.close() }
                        onNavigateToHome()
                    }
                    ProfileMenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces) {
                        scope.launch { drawerState.close() }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout menu item
                    ProfileMenuItemRow("Logout", AndroidR.drawable.ic_lock_power_off) {
                        scope.launch { drawerState.close() }
                        showLogoutDialog = true
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
                            text = "Profile",
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
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.Red
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
                            selected = true,
                            onClick = { /* Already on profile */ }
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
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF5C5CFF))
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Text(
                            text = userProfile?.name?.firstOrNull()?.uppercase() ?: "U",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF5C5CFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userProfile?.name ?: "User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "№ ${user?.id?.take(8) ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_dialog_email,
                        title = "Email",
                        value = user?.email ?: "Not available"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_menu_manage,
                        title = "Role",
                        value = userProfile?.role?.replaceFirstChar { it.uppercase() } ?: "Technician"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_menu_slideshow,
                        title = "Status",
                        value = userProfile?.status?.replaceFirstChar { it.uppercase() } ?: "Active"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Logout button at the bottom
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

