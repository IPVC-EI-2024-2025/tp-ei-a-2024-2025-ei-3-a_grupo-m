package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import kotlinx.coroutines.launch
import android.R as AndroidR
import com.example.project_we_fix_it.composables.DashboardMenuItemRow
import com.example.project_we_fix_it.composables.WeFixItAppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToBreakdownReporting: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var isDarkThemeEnabled by remember { mutableStateOf(false) }

    WeFixItAppScaffold(
        title = "Settings",
        currentRoute = "settings",
        navController = navController,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onOpenSettings = onOpenSettings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAssignments = onNavigateToAssignments,
        onNavigateToBreakdownReporting = onNavigateToBreakdownReporting,
        onLogout = onLogout,
        authViewModel = authViewModel
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SettingsItem(
                    title = "Language",
                    description = "Choose your language",
                    showArrow = true,
                    onClick = { /* Open language selection */ }
                )

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                SettingsItem(
                    title = "Notifications",
                    description = "Notifications preferences",
                    showArrow = true,
                    onClick = { /* Open notification settings */ }
                )

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                SettingsItem(
                    title = "Account Settings",
                    description = "Manage your account settings",
                    showArrow = true,
                    onClick = { /* Open account settings */ }
                )

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Dark Theme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Change to dark theme",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isDarkThemeEnabled,
                        onCheckedChange = { isDarkThemeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5C5CFF),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_dialog_info),
                        contentDescription = "About",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "About",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        if (showArrow) {
            Icon(
                painter = painterResource(id = AndroidR.drawable.ic_media_play),
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}