package com.example.project_we_fix_it

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Menu - Technicians",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    MenuItemRow("Home", AndroidR.drawable.ic_menu_agenda) {
                        scope.launch { drawerState.close() }
                        onNavigateToHome()
                    }
                    MenuItemRow("Profile", AndroidR.drawable.ic_menu_myplaces) {
                        scope.launch { drawerState.close() }

                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenSettings) {
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
            },
            bottomBar = {
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
                            label = { Text("Profile") },
                            selected = true,
                            onClick = { /* Already on profile */ }
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = AndroidR.drawable.ic_menu_agenda),
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") },
                            selected = false,
                            onClick = onNavigateToHome
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = AndroidR.drawable.ic_popup_reminder),
                                    contentDescription = "Notifications"
                                )
                            },
                            label = { Text("Notifications") },
                            selected = false,
                            onClick = onNavigateToNotifications
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF5C5CFF))
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Text(
                            text = "J",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF5C5CFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Joaquim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "№ 32432",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_dialog_email,
                        title = "Email",
                        value = "Joaquim@email.com"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_menu_manage,
                        title = "Speciality",
                        value = "Eletrical Technician"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        iconResId = AndroidR.drawable.ic_menu_slideshow,
                        title = "Block",
                        value = "3ᵃ Block"
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    iconResId: Int,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = title,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}