package com.example.project_we_fix_it

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.ProfileInfoItem
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    commonActions: CommonScreenActions,
    onNavigateToEditProfile: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            commonActions.logout()
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ProfileScreen", "Loading profile data")
        authViewModel.loadUserProfile()
    }

    if (authState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val userProfile = authState.userProfile
    val user = authState.user

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
                    Text("Yes", color = MaterialTheme.colorScheme.error)
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
        showBackButton = true,
        onBackClick = commonActions.onBackClick
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
                        .background(MaterialTheme.colorScheme.primary)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Text(
                        text = userProfile?.name?.firstOrNull()?.uppercase() ?: "U",
                        color = MaterialTheme.colorScheme.onPrimary,
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
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimary,
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    title = "Email",
                    value = user?.email ?: "Not available"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    icon = Icons.Default.Work,
                    title = "Role",
                    value = userProfile?.role?.replaceFirstChar { it.uppercase() } ?: "Technician"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    icon = Icons.Default.Info,
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
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
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