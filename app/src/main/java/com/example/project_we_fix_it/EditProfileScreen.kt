package com.example.project_we_fix_it

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    var name by remember { mutableStateOf("User") }
    var email by remember { mutableStateOf("user@email.com") }
    var phoneNumber by remember { mutableStateOf("123-456-7890") }
    var password by remember { mutableStateOf("password123*") }
    var passwordVisible by remember { mutableStateOf(false) }
    var speciality by remember { mutableStateOf("Electrical Technician") }
    var block by remember { mutableStateOf("3ᵃ Block") }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToProfile) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
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
                        text = "U",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF5C5CFF))
                        .padding(4.dp)
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    Icon(
                        painter = painterResource(id = AndroidR.drawable.ic_menu_camera),
                        contentDescription = "Change profile picture",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "№ 32432",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ProfileTextField(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    iconResId = AndroidR.drawable.ic_menu_myplaces
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    iconResId = AndroidR.drawable.ic_dialog_email
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    iconResId = AndroidR.drawable.ic_menu_call
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) AndroidR.drawable.ic_partial_secure
                                    else AndroidR.drawable.ic_secure
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C5CFF),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Speciality",
                    value = speciality,
                    onValueChange = { speciality = it },
                    iconResId = AndroidR.drawable.ic_menu_manage
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Block",
                    value = block,
                    onValueChange = { block = it },
                    iconResId = AndroidR.drawable.ic_menu_slideshow
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C5CFF)
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconResId: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = Color.Gray
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF5C5CFF),
            unfocusedBorderColor = Color.LightGray
        )
    )
}