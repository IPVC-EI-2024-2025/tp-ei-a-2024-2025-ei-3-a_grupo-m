package com.example.project_we_fix_it

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.composables.WeFixItAppScaffold
import com.example.project_we_fix_it.nav.CommonScreenActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    commonActions: CommonScreenActions,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var isDarkThemeEnabled by remember { mutableStateOf(false) }
    var showLanguageDropdown by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val resources = LocalContext.current.resources

    val currentLanguage = remember {
        mutableStateOf(
            if (configuration.locale.language == "pt") "Português" else "English"
        )
    }

    // Função para mudar o idioma
    fun changeLanguage(language: String) {
        val locale = when (language) {
            "Português" -> Locale("pt")
            "Português" -> Locale("pt")
            else -> Locale("en")
        }

        Locale.setDefault(locale)
        val config = Configuration(configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        currentLanguage.value = language
        showLanguageDropdown = false
    }

    WeFixItAppScaffold(
        title = "Settings",
        currentRoute = "settings",
        navController = commonActions.navController,
        onNavigateToProfile = commonActions.navigateToProfile,
        onNavigateToHome = commonActions.navigateToHome,
        onOpenSettings = commonActions.openSettings,
        onNavigateToNotifications = commonActions.navigateToNotifications,
        onNavigateToAssignments = commonActions.navigateToAssignments,
        onNavigateToBreakdownReporting = commonActions.navigateToBreakdownReporting,
        onNavigateToMessages = commonActions.navigateToMessages,
        onNavigateToAdminDashboard = commonActions.navigateToAdminDashboard,
        onNavigateToAdminUsers = commonActions.navigateToAdminUsers,
        onNavigateToAdminEquipment = commonActions.navigateToAdminEquipment,
        onNavigateToAdminBreakdowns = commonActions.navigateToAdminBreakdowns,
        onNavigateToAdminAssignments = commonActions.navigateToAdminAssignments,
        onLogout = commonActions.logout,
        authViewModel = authViewModel,
        showBackButton = true,
        onBackClick = commonActions.onBackClick,
        notificationViewModel = hiltViewModel()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.clickable { showLanguageDropdown = true }
                    ) {
                        Text(
                            text = stringResource(R.string.language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.choose_language),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentLanguage.value,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = showLanguageDropdown,
                        onDismissRequest = { showLanguageDropdown = false },
                        modifier = Modifier.fillMaxWidth(fraction = 0.8f)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.english)) },
                            onClick = {
                                changeLanguage("English")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.portuguese)) },
                            onClick = {
                                changeLanguage("Português")
                            }
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                SettingsItem(
                    title = "Notifications",
                    description = "Notifications preferences",
                    showArrow = true,
                    onClick = { commonActions.navigateToNotifications() }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                SettingsItem(
                    title = "Account Settings",
                    description = "Manage your account settings",
                    showArrow = true,
                    onClick = { commonActions.navigateToProfile() }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = isDarkThemeEnabled,
                        onCheckedChange = { isDarkThemeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Handle about */ }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}