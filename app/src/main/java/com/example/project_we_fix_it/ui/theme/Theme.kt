package com.example.project_we_fix_it.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.project_we_fix_it.WeFixItColorScheme

@Composable
fun ProjectWeFixItTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WeFixItColorScheme,
        typography = Typography,
        content = content
    )
}