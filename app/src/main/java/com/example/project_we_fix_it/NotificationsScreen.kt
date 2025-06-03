package com.example.project_we_fix_it

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.nav.CommonScreenActions

@Composable
fun NotificationsScreen(
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    // Replace with your actual notifications UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Notifications Screen")
    }
}