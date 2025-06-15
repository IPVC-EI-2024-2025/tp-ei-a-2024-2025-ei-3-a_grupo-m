package com.example.project_we_fix_it.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileMenuItemRow(
    text: String,
    icon: ImageVector,  
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = if (text == "Logout") Color.Red else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = if (text == "Logout") Color.Red else MaterialTheme.colorScheme.onSurface
        )
    }
}