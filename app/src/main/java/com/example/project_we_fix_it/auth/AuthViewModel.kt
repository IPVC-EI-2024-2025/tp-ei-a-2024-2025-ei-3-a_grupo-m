package com.example.project_we_fix_it.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: UserInfo? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    val currentUserId: String?
        get() = authState.value.user?.id

    private val supabaseRepository = SupabaseRepository()

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting auth status check")
                _authState.value = AuthState(isLoading = true)

                authRepository.loadSession()

                val currentUser = authRepository.getCurrentUser()
                Log.d("AuthViewModel", "Current user after load: ${currentUser?.id}")

                if (currentUser != null) {
                    val refreshResult = authRepository.refreshSession()
                    refreshResult.fold(
                        onSuccess = {
                            Log.d("AuthViewModel", "Session refreshed successfully")
                        },
                        onFailure = { error ->
                            Log.w("AuthViewModel", "Session refresh failed: ${error.message}")
                            authRepository.logout()
                            _authState.value = AuthState(isLoading = false, isLoggedIn = false)
                            return@launch
                        }
                    )
                }

                val user = authRepository.getCurrentUser()
                val isLoggedIn = user != null
                val userProfile = if (isLoggedIn) {
                    authRepository.getCurrentUserProfile()
                } else null

                Log.d("AuthViewModel", "Final auth state - User: ${user?.id}, LoggedIn: $isLoggedIn")

                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = isLoggedIn,
                    user = user,
                    userProfile = userProfile
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Auth status check failed", e)
                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = false,
                    error = "Session error: ${e.message}"
                )
            }
        }
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Starting login for: $email")
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    Log.d("AuthViewModel", "Login successful for user: ${user.id}")
                    val userProfile = authRepository.getCurrentUserProfile()
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        userProfile = userProfile,
                        error = null
                    )
                },
                onFailure = { exception ->
                    Log.e("AuthViewModel", "Login failed", exception)
                    val errorMessage = when {
                        exception.message?.contains("Invalid login credentials") == true ->
                            "Email or password is incorrect"
                        exception.message?.contains("Email not confirmed") == true ->
                            "Please confirm your email before logging in"
                        else -> "Login failed. Please try again"
                    }
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = errorMessage
                    )
                }
            )
        }
    }

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.register(email, password, fullName)
            result.fold(
                onSuccess = { user ->
                    val userProfile = authRepository.getCurrentUserProfile()
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        userProfile = userProfile,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = exception.message
                    )
                }
            )
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.resetPassword(email)
            result.fold(
                onSuccess = {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Starting logout")
            _authState.value = _authState.value.copy(isLoading = true)

            try {
                authRepository.logout()
                // Reset to clean state
                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = false,
                    user = null,
                    userProfile = null,
                    error = null
                )
                Log.d("AuthViewModel", "Logout completed successfully")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout failed", e)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Logout failed: ${e.message}"
                )
            }
        }
    }


    fun loadUserProfile() {
        Log.d("AuthVM", "loadUserProfile() called")
        viewModelScope.launch {
            try {
                _authState.value.user?.id?.let { userId ->
                    Log.d("AuthVM", "Loading profile for user: $userId")
                    val profile = supabaseRepository.getUserProfile(userId)
                    Log.d("AuthVM", "Profile loaded: ${profile?.name}")

                    _authState.update { currentState ->
                        currentState.copy(userProfile = profile)
                    }
                    Log.d("AuthVM", "AuthState updated with new profile")
                } ?: run {
                    Log.w("AuthVM", "No user ID available to load profile")
                }
            } catch (e: Exception) {
                Log.e("AuthVM", "Error loading profile: ${e.stackTraceToString()}")
            }
        }
    }

    fun adminUpdateUser(profile: UserProfile) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.adminUpdateUserProfile(profile)
            result.fold(
                onSuccess = {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "User updated successfully"
                    )
                    if (authState.value.user?.id == profile.user_id) {
                        loadUserProfile()
                    }
                },
                onFailure = { e ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

}