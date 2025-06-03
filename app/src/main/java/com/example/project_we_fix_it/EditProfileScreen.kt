package com.example.project_we_fix_it

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.nav.CommonScreenActions
import com.example.project_we_fix_it.supabase.UserProfile
import com.example.project_we_fix_it.viewModels.UserProfileViewModel
import kotlinx.coroutines.launch
import android.R as AndroidR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    commonActions: CommonScreenActions,
    navController: NavController,
    onNavigateToProfile: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val TAG = "EditProfileScreen"

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val isLoading by profileViewModel.isLoading.collectAsStateWithLifecycle()
    val currentProfile = profileState ?: authState.userProfile
    val user = authState.user
    val coroutineScope = rememberCoroutineScope()

    // Initialize fields with actual user data or empty strings
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var speciality by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSaveSuccess by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it }
    }

    LaunchedEffect(currentProfile, user) {
        name = currentProfile?.name ?: ""
        email = user?.email ?: ""
        phoneNumber = currentProfile?.phone ?: ""
        speciality = currentProfile?.role ?: ""
        block = currentProfile?.location ?: ""
    }


    fun saveProfile() {
        Log.d(TAG, "saveProfile() called")
        val updatedProfile = currentProfile?.copy(
            name = name,
            phone = phoneNumber,
            role = speciality,
            location = block
        ) ?: UserProfile(
            user_id = user?.id ?: "",
            name = name,
            phone = phoneNumber,
            role = speciality,
            location = block,
            status = currentProfile?.status ?: "active"
        )

        Log.d(TAG, "Updating profile with: $updatedProfile")
        profileViewModel.updateProfile(updatedProfile)

        coroutineScope.launch {
            Log.d(TAG, "Refreshing auth state...")
            authViewModel.loadUserProfile()
        }
        showSaveSuccess = true
        Log.d(TAG, "Profile update flow completed")
    }


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF5C5CFF))
        }
    }

    profileViewModel.error.collectAsStateWithLifecycle().value?.let { error ->
        AlertDialog(
            onDismissRequest = { profileViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(
                    onClick = { profileViewModel.clearError() }
                ) {
                    Text("OK")
                }
            }
        )
    }



    if (showSaveSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSaveSuccess = false
                onBack()
            },
            title = { Text("Profile Updated") },
            text = { Text("Your profile has been successfully updated.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveSuccess = false
                        onBack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        text = name.firstOrNull()?.uppercase() ?: "U",
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
                text = "№ ${user?.id?.take(8) ?: "N/A"}",
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
                    placeholder = currentProfile?.name ?: "Name not defined",
                    onValueChange = { name = it },
                    iconResId = AndroidR.drawable.ic_menu_myplaces
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Email",
                    value = email,
                    placeholder = user?.email ?: "Email not defined" ,
                    onValueChange = { email = it },
                    iconResId = AndroidR.drawable.ic_dialog_email,
                    enabled = false // Email shouldn't be editable directly
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Phone Number",
                    value = phoneNumber,
                    placeholder = currentProfile?.phone ?: "Phone not defined",
                    onValueChange = { phoneNumber = it },
                    iconResId = AndroidR.drawable.ic_menu_call
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password (leave empty to keep current)") },
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
                    placeholder = currentProfile?.role ?: "Speciality not defined",
                    onValueChange = { speciality = it },
                    iconResId = AndroidR.drawable.ic_menu_manage
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "Block/Location",
                    value = block,
                    placeholder = currentProfile?.location ?: "Block/Location not defined",
                    onValueChange = { block = it },
                    iconResId = AndroidR.drawable.ic_menu_slideshow
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { saveProfile() },
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
    iconResId: Int,
    placeholder: String = "",
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = Color.Gray
            )
        },
        singleLine = true,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF5C5CFF),
            unfocusedBorderColor = Color.LightGray
        )
    )
}