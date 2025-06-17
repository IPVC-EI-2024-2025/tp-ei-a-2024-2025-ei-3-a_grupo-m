package com.example.project_we_fix_it.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.project_we_fix_it.R

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
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Compact header
            Text(
                text = "Menu - $userRole",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )

            // Main navigation items
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.home)) },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                selected = false,
                onClick = onHomeClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.profile)) },
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                selected = false,
                onClick = onProfileClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.notifications)) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                selected = false,
                onClick = onNotificationsClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.my_assignments)) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                selected = false,
                onClick = onAssignmentsClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.messages)) },
                icon = { Icon(Icons.Default.Message, contentDescription = null) },
                selected = false,
                onClick = onMessagesClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (userRole.equals("admin", ignoreCase = true)) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = stringResource(R.string.admin),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.admin_dashboard)) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    selected = false,
                    onClick = onAdminDashboardClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.admin_equipment)) },
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    selected = false,
                    onClick = onAdminEquipmentClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.admin_breakdowns)) },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    selected = false,
                    onClick = onAdminBreakdownsClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.admin_users)) },
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    selected = false,
                    onClick = onAdminUsersClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.admin_assignments)) },
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) },
                    selected = false,
                    onClick = onAdminAssignmentsClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Divider(modifier = Modifier.padding(bottom = 8.dp))

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.settings)) },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                selected = false,
                onClick = onSettingsClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.logout)) },
                icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red) },
                selected = false,
                onClick = onLogoutClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}