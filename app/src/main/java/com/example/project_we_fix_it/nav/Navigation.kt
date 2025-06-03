package com.example.project_we_fix_it.nav

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val MESSAGES = "messages"
    const val BREAKDOWN_REPORTING = "report"
    const val ASSIGNMENTS = "assignments"
    const val BREAKDOWN_DETAILS = "breakdown/{id}"
    const val MY_BREAKDOWNS = "my_breakdowns"
    const val EDIT_PROFILE = "edit_profile"
    const val CHAT = "chat/{chatId}"
    const val REGISTER = "register"
    const val PASSWORD_RECOVERY = "password_recovery"
}

data class CommonScreenActions(
    val navController: NavController,
    val navigateToProfile: () -> Unit,
    val navigateToHome: () -> Unit,
    val logout: () -> Unit,
    val navigateToNotifications: () -> Unit,
    val openSettings: () -> Unit,
    val navigateToAssignments: () -> Unit,
    val navigateToBreakdownReporting: () -> Unit,
    val navigateToMessages: () -> Unit,
    val onBackClick: () -> Unit,
    val showBackButton: Boolean = false,
) {
    fun navigateToBreakdownDetails(id: String) {
        navController.navigate(Routes.BREAKDOWN_DETAILS.replace("{id}", id))
    }
}

class AppNavigator(val navController: NavController) {
    fun navigateTo(route: String, builder: NavOptionsBuilder.() -> Unit = {}) =
        navController.navigate(route, builder)

    fun popBackStack() = navController.popBackStack()

    fun navigateToLogin() = navigateTo(Routes.LOGIN) { popUpTo(0) }
    fun navigateToDashboard() = navigateTo(Routes.DASHBOARD)
    fun navigateToProfile() = navigateTo(Routes.PROFILE)
    fun navigateToSettings() = navigateTo(Routes.SETTINGS)
    fun navigateToNotifications() = navigateTo(Routes.NOTIFICATIONS)
    fun navigateToMessages() = navigateTo(Routes.MESSAGES)
    fun navigateToBreakdownReporting() = navigateTo(Routes.BREAKDOWN_REPORTING)
    fun navigateToAssignments() = navigateTo(Routes.ASSIGNMENTS)
    fun navigateToBreakdownDetails(id: String) = navigateTo(Routes.BREAKDOWN_DETAILS.replace("{id}", id))
    fun navigateToMyBreakdowns() = navigateTo(Routes.MY_BREAKDOWNS)
    fun navigateToEditProfile() = navigateTo(Routes.EDIT_PROFILE)
    fun navigateToChat(chatId: String) {
        if (chatId.isBlank()) {
            println("Error: Empty chatId provided")
            return
        }
        val route = Routes.CHAT.replace("{chatId}", chatId)
        println("Navigating to: $route")
        navigateTo(route)
    }
    fun navigateToRegister() = navigateTo(Routes.REGISTER)
    fun navigateToPasswordRecovery() = navigateTo(Routes.PASSWORD_RECOVERY)

    fun getCommonActions(showBackButton: Boolean = false) = CommonScreenActions(
        navController = navController,
        navigateToProfile = ::navigateToProfile,
        navigateToHome = ::navigateToDashboard,
        logout = ::navigateToLogin,
        navigateToNotifications = ::navigateToNotifications,
        openSettings = ::navigateToSettings,
        navigateToAssignments = ::navigateToAssignments,
        navigateToBreakdownReporting = ::navigateToBreakdownReporting,
        navigateToMessages = ::navigateToMessages,
        onBackClick = ::popBackStack,
        showBackButton = showBackButton
    )
}