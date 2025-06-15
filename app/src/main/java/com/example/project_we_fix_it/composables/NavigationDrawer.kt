package com.example.project_we_fix_it.composables

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.project_we_fix_it.R
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

        DashboardMenuItemRow(stringResource(R.string.home), AndroidR.drawable.ic_menu_agenda, onHomeClick)
        DashboardMenuItemRow(stringResource(R.string.profile), AndroidR.drawable.ic_menu_myplaces, onProfileClick)
        DashboardMenuItemRow(stringResource(R.string.my_breakdowns), AndroidR.drawable.ic_menu_report_image, onMyBreakdownsClick)
        DashboardMenuItemRow(stringResource(R.string.notifications), AndroidR.drawable.ic_popup_reminder, onNotificationsClick)
        DashboardMenuItemRow(stringResource(R.string.my_assignments), AndroidR.drawable.ic_menu_add, onAssignmentsClick)
        DashboardMenuItemRow(stringResource(R.string.settings), AndroidR.drawable.ic_menu_preferences, onSettingsClick)
        DashboardMenuItemRow(stringResource(R.string.messages), AndroidR.drawable.ic_menu_search, onMessagesClick)

        if (userRole == "admin") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(R.string.admin),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            DashboardMenuItemRow(stringResource(R.string.admin_dashboard), AndroidR.drawable.ic_menu_manage, onAdminDashboardClick)
            DashboardMenuItemRow(stringResource(R.string.admin_equipment), AndroidR.drawable.ic_menu_manage, onAdminEquipmentClick)
            Log.d("WeFixItNavigationDrawer", "Admin Equipment clicked")
            DashboardMenuItemRow(stringResource(R.string.admin_breakdowns), AndroidR.drawable.ic_menu_manage, onAdminBreakdownsClick)
            DashboardMenuItemRow(stringResource(R.string.admin_users), AndroidR.drawable.ic_menu_manage, onAdminUsersClick)
            DashboardMenuItemRow(stringResource(R.string.admin_assignments), AndroidR.drawable.ic_menu_manage, onAdminAssignmentsClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        DashboardMenuItemRow(stringResource(R.string.logout), AndroidR.drawable.ic_lock_power_off, onLogoutClick)
    }
}

