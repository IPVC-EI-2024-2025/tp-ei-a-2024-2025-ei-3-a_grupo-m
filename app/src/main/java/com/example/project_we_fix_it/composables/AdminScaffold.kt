import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.composables.WeFixItAppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    title: String,
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    onNavigateToAdminUsers: () -> Unit,
    onNavigateToAdminEquipment: () -> Unit,
    onNavigateToAdminBreakdowns: () -> Unit,
    onNavigateToAdminAssignments: () -> Unit,
    onLogout: () -> Unit,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = { navController.popBackStack() },
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    WeFixItAppScaffold(
        title = title,
        currentRoute = "admin",
        navController = navController,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onOpenSettings = onOpenSettings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAssignments = onNavigateToAssignments,
        onNavigateToBreakdownReporting = onNavigateToBreakdownReporting,
        onNavigateToMessages = onNavigateToMessages,
        onNavigateToAdminDashboard = onNavigateToAdminDashboard,
        onNavigateToAdminUsers = onNavigateToAdminUsers,
        onNavigateToAdminEquipment = onNavigateToAdminEquipment,
        onNavigateToAdminBreakdowns = onNavigateToAdminBreakdowns,
        onNavigateToAdminAssignments = onNavigateToAdminAssignments,
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        actions = actions,
        onLogout = onLogout,
        content = content,
        notificationViewModel = hiltViewModel()
    )
}