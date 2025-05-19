package com.example.project_we_fix_it

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(
    navController: NavHostController,
    onNavigateToRegister: () -> Unit,
    onNavigateToPasswordRecovery: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        LogoSection()

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Email",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Insert your email here") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WeFixItGrey),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = WeFixItBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Password",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Insert your password here") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WeFixItGrey),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = WeFixItBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToPasswordRecovery,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot your password?",
                color = WeFixItBlue
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WeFixItBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? "
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Register here!",
                    color = WeFixItBlue
                )
            }
        }
    }
}

@Composable
fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // This is not the logo btw
        Text(
            text = "WE",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "FIX",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "IT",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_manage),
            contentDescription = "Tools",
            tint = WeFixItBlue,
            modifier = Modifier.size(48.dp)
        )
    }
}
