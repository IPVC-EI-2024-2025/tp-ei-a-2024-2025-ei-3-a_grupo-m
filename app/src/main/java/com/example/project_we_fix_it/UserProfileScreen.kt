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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.ProfileInfoItem
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.composables.WeFixItBottomBar
import com.example.project_we_fix_it.composables.WeFixItNavigationDrawer
import com.example.project_we_fix_it.composables.WeFixItTopAppBar
import kotlinx.coroutines.launch
import android.R as AndroidR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
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

    WeFixItAppScaffold(
        title = "Profile",
        currentRoute = "profile",
        navController = navController,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onOpenSettings = onOpenSettings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAssignments = onNavigateToAssignments,
        onNavigateToBreakdownReporting = onNavigateToBreakdownReporting,
        onLogout = onLogout
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


