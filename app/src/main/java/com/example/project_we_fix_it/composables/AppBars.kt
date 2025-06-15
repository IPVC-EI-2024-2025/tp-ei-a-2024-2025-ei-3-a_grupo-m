package com.example.project_we_fix_it.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.R as AndroidR
import com.example.project_we_fix_it.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeFixItTopAppBar(
    title: String,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showLogout: Boolean = false,
    onLogoutClick: () -> Unit = {},
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_revert),
                        contentDescription = "Back"
                    )
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                        contentDescription = "Menu"
                    )
                }
            }
        },
        actions = {
            actions()

            if (showLogout) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Red
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(id = AndroidR.drawable.ic_menu_preferences),
                    contentDescription = "Settings"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun WeFixItBottomBar(
    currentRoute: String,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    unreadCount: Int = 0
) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_myplaces),
                        contentDescription = "Profile"
                    )
                },
                label = { Text(stringResource(R.string.profile)) },
                selected = currentRoute == "profile",
                onClick = onProfileClick
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_agenda),
                        contentDescription = "Home"
                    )
                },
                label = { Text(stringResource(R.string.home)) },
                selected = currentRoute == "home",
                onClick = onHomeClick
            )
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge {
                                    Text(unreadCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = AndroidR.drawable.ic_popup_reminder),
                            contentDescription = "Notifications"
                        )
                    }
                },
                label = { Text(stringResource(R.string.notifications)) },
                selected = currentRoute == "notifications",
                onClick = onNotificationsClick
            )
        }
    }
}