package com.example.project_we_fix_it.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.R as AndroidR

@Composable
fun WeFixItNavigationDrawer(
    userRole: String = "Technician",
    onAdminDashboardClick: () -> Unit = {},
    onAdminEquipmentClick: () -> Unit = {},
    onAdminBreakdownsClick: () -> Unit = {},
    onAdminUsersClick: () -> Unit = {},
    onAdminAssignmentsClick: () -> Unit = {},
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMyBreakdownsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAssignmentsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Menu - $userRole",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        DashboardMenuItemRow("Home", AndroidR.drawable.ic_menu_agenda, onHomeClick)
        DashboardMenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces, onProfileClick)
        DashboardMenuItemRow("My breakdowns", AndroidR.drawable.ic_menu_report_image, onMyBreakdownsClick)
        DashboardMenuItemRow("Notifications", AndroidR.drawable.ic_popup_reminder, onNotificationsClick)
        DashboardMenuItemRow("My Assignments", AndroidR.drawable.ic_menu_add, onAssignmentsClick)
        DashboardMenuItemRow("Settings", AndroidR.drawable.ic_menu_preferences, onSettingsClick)
        DashboardMenuItemRow("Messages", AndroidR.drawable.ic_menu_search, onMessagesClick)

        if (userRole == "admin") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Admin",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            DashboardMenuItemRow("Admin Dashboard", AndroidR.drawable.ic_menu_manage, onAdminDashboardClick)
            DashboardMenuItemRow("Admin Equipment", AndroidR.drawable.ic_menu_manage, onAdminEquipmentClick)
            DashboardMenuItemRow("Admin Breakdowns", AndroidR.drawable.ic_menu_manage, onAdminBreakdownsClick)
            DashboardMenuItemRow("Admin Users", AndroidR.drawable.ic_menu_manage, onAdminUsersClick)
            DashboardMenuItemRow("Admin Assignments", AndroidR.drawable.ic_menu_manage, onAdminAssignmentsClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        DashboardMenuItemRow("Logout", AndroidR.drawable.ic_lock_power_off, onLogoutClick)
    }
}

