package com.example.project_we_fix_it.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
    onMenuClick: () -> Unit = {},
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            actions()
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
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
    onMenuClick: () -> Unit,
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
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                },
                label = { Text("Menu") },
                selected = false,
                onClick = onMenuClick
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("Home") },
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
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                label = { Text("Notifications") },
                selected = currentRoute == "notifications",
                onClick = onNotificationsClick
            )
        }
    }
}