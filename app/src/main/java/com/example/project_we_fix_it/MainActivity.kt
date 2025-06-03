package com.example.project_we_fix_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_we_fix_it.nav.AppNavigator
import com.example.project_we_fix_it.nav.Routes
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
    val navigator = remember { AppNavigator(navController) }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = navigator::navigateToRegister,
                onNavigateToPasswordRecovery = navigator::navigateToPasswordRecovery,
                onLoginSuccess = navigator::navigateToDashboard,
                navController = navController,
                authViewModel = hiltViewModel()
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = navigator::navigateToLogin,
                onRegisterSuccess = navigator::navigateToLogin,
                navController = navController,
                authViewModel = hiltViewModel()
            )
        }
        composable(Routes.PASSWORD_RECOVERY) {
            PasswordRecoveryScreen(
                onNavigateToLogin = navigator::navigateToLogin,
                navController = navController,
                authViewModel = hiltViewModel()
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                commonActions = navigator.getCommonActions(),
            )
        }
        composable(Routes.PROFILE) {
            UserProfileScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                onNavigateToEditProfile = navigator::navigateToEditProfile
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                navController = navController,
                onNavigateToProfile = navigator::navigateToProfile,
                onBack = navigator::popBackStack,
                onNavigateToHome = navigator::navigateToDashboard,
                onNavigateToNotifications = navigator::navigateToNotifications
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                commonActions = navigator.getCommonActions(showBackButton = true)
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                commonActions = navigator.getCommonActions(showBackButton = true)
            )
        }
        composable(Routes.MESSAGES) {
            MessagesScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                navigator = navigator,
            )
        }
        composable(Routes.CHAT) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            println("ChatScreen opened with ID: $chatId")
            ChatScreen(
                chatId = chatId,
                commonActions = navigator.getCommonActions(showBackButton = true)
            )
        }
        composable(Routes.BREAKDOWN_REPORTING) {
            BreakdownReportingScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                onSave = { /* Handle save */ }
            )
        }
        composable(Routes.BREAKDOWN_DETAILS) { backStackEntry ->
            BreakdownDetailsScreen(
                breakdownId = backStackEntry.arguments?.getString("id") ?: "",
                commonActions = navigator.getCommonActions(showBackButton = true),
                onSave = { /* Handle save */ }
            )
        }
        composable(Routes.ASSIGNMENTS) {
            MyAssignmentsScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                onBreakdownClick = navigator::navigateToBreakdownDetails
            )
        }
        composable(Routes.MY_BREAKDOWNS) {
            MyBreakdownsScreen(
                commonActions = navigator.getCommonActions(showBackButton = true),
                onBreakdownClick = navigator::navigateToBreakdownDetails
            )
        }
    }
}