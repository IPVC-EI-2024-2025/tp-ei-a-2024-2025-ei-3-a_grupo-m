package com.example.project_we_fix_it.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import io.ktor.websocket.Frame
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeFixItAppScaffold(
    title: String,
    currentRoute: String,
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onNavigateToMessages: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Handle auth state changes
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            onLogout()
        }
    }

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
                    Frame.Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                WeFixItNavigationDrawer(
                    userRole = "Technician",
                    onHomeClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHome()
                    },
                    onProfileClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    },
                    onMyBreakdownsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("my_breakdowns")
                    },
                    onNotificationsClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToNotifications()
                    },
                    onAssignmentsClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAssignments()
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    },
                    onMessagesClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToMessages()
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        showLogoutDialog = true
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                WeFixItTopAppBar(
                    title = title,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSettingsClick = onOpenSettings,
                    showLogout = true,
                    showBackButton = showBackButton,
                    onBackClick = onBackClick,
                    onLogoutClick = { showLogoutDialog = true },
                    actions = actions
                )
            },
            bottomBar = {
                WeFixItBottomBar(
                    currentRoute = currentRoute,
                    onProfileClick = onNavigateToProfile,
                    onHomeClick = onNavigateToHome,
                    onNotificationsClick = onNavigateToNotifications
                )
            },
            content = content
        )
    }
}