package com.example.project_we_fix_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_we_fix_it.ui.theme.ProjectWeFixItTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectWeFixItTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToPasswordRecovery = { navController.navigate("password_recovery") },
                onLoginSuccess = { navController.navigate("dashboard") }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = { navController.navigate("login") }
            )
        }
        composable("password_recovery") {
            PasswordRecoveryScreen(
                navController = navController,
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onOpenSettings = { navController.navigate("settings") },
                onOpenChat = { navController.navigate("messages") },
                onNavigateToBreakdownReporting = { navController.navigate("report") },
                onNavigateToAssignments = { navController.navigate("assignments") },
                onOpenMenu = { /* Show menu */ }
            )
        }

        // Add these new routes
        composable("profile") {
            UserProfileScreen(
                navController = navController,
                onNavigateToHome = { navController.navigate("dashboard") },
                onOpenSettings = { navController.navigate("settings") },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                onNavigateToHome = { navController.navigate("dashboard") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }

        composable("messages") {
            MessagesScreen(navController = navController)
        }
        composable("chat/{chatId}") { backStackEntry ->
            ChatScreen(
                navController = navController,
                chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            )
        }
        composable("report") {
            BreakdownReportingScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onSave = { /* Save and navigate */ },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToHome = { navController.popBackStack("dashboard", false) },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }
        composable("breakdown/{id}") { backStackEntry ->
            BreakdownDetailsScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onSave = { /* Save and navigate */ },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToHome = { navController.popBackStack("dashboard", false) },
                onNavigateToNotifications = { navController.navigate("notifications") }
            )
        }
        composable("assignments") {
            MyAssignmentsScreen(
                navController = navController,
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToHome = { navController.navigate("dashboard") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onBreakdownClick = { breakdownId -> navController.navigate("breakdown/$breakdownId") }
            )
        }
    }
}

@Composable
fun ProjectWeFixItTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WeFixItColorScheme,
        typography = Typography,
        content = content
    )
}