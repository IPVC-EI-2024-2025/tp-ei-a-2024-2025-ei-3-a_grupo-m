package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.ui.theme.WeFixItGrey
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBreakdownsScreen(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onBreakdownClick: (String) -> Unit,
    viewModel: MyBreakdownsViewModel = viewModel()
) {
    val breakdowns by viewModel.myBreakdowns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadMyBreakdowns()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Breakdowns",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = AndroidR.drawable.ic_menu_sort_by_size),
                            contentDescription = "Filter"
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
                        selected = false,
                        onClick = onNavigateToProfile
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5C5CFF))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "List of my breakdowns",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(breakdowns) { breakdown ->
                            MyBreakdownCard(
                                breakdown = breakdown,
                                onClick = {
                                    onBreakdownClick(breakdown.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBreakdownCard(
    breakdown: BreakdownItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeFixItGrey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = breakdown.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breakdown.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Pending",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.Red)
                )
            }
        }
    }
}