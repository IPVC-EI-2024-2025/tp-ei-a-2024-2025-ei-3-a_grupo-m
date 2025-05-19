package com.example.project_we_fix_it

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PasswordRecoveryScreen(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("email") }
    var emailValue by remember { mutableStateOf("") }
    var phoneValue by remember { mutableStateOf("") }

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
            text = "Forgot your password?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose the method to recover your password!",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { selectedMethod = "email" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMethod == "email") WeFixItBlue else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_email),
                        contentDescription = "Email",
                        tint = if (selectedMethod == "email") Color.White else Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Email",
                        color = if (selectedMethod == "email") Color.White else Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { selectedMethod = "phone" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMethod == "phone") WeFixItBlue else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_dialer),
                        contentDescription = "Phone",
                        tint = if (selectedMethod == "phone") Color.White else Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Phone number",
                        color = if (selectedMethod == "phone") Color.White else Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (selectedMethod) {
            "email" -> {
                OutlinedTextField(
                    value = emailValue,
                    onValueChange = { emailValue = it },
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
            }
            "phone" -> {
                OutlinedTextField(
                    value = phoneValue,
                    onValueChange = { phoneValue = it },
                    placeholder = { Text("Insert your phone number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(WeFixItGrey),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = WeFixItBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Recovery logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WeFixItBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onNavigateToLogin
        ) {
            Text(
                text = "Back to Login",
                color = WeFixItBlue,
                fontSize = 16.sp
            )
        }
    }
}
