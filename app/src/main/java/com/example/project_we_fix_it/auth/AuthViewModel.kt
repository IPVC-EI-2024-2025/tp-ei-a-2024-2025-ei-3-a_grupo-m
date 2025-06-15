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
    private val TAG = "AuthVM"
    // Initialize with loading state
    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUserId: String?
        get() = authState.value.user?.id

    private val supabaseRepository = SupabaseRepository()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _authState.value = AuthState(isLoading = true)

        viewModelScope.launch {
            try {
                authRepository.loadSession()

                if (authRepository.isUserLoggedIn()) {
                    authRepository.refreshSession().fold(
                        onSuccess = { /* Session refreshed */ },
                        onFailure = {
                            authRepository.logout()
                        }
                    )
                }

                val isLoggedIn = authRepository.isUserLoggedIn()
                val user = authRepository.getCurrentUser()
                val userProfile = if (isLoggedIn) authRepository.getCurrentUserProfile() else null

                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = isLoggedIn,
                    user = user,
                    userProfile = userProfile
                )
            } catch (e: Exception) {
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
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.login(email, password)
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
            _authState.value = _authState.value.copy(isLoading = true)

            val result = authRepository.logout()
            result.fold(
                onSuccess = {
                    _authState.value = AuthState(isLoggedIn = false)
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

    fun loadUserProfile() {
        Log.d(TAG, "loadUserProfile() called")
        viewModelScope.launch {
            try {
                _authState.value.user?.id?.let { userId ->
                    Log.d(TAG, "Loading profile for user: $userId")
                    val profile = supabaseRepository.getUserProfile(userId)
                    Log.d(TAG, "Profile loaded: ${profile?.name}")

                    _authState.update { currentState ->
                        currentState.copy(userProfile = profile)
                    }
                    Log.d(TAG, "AuthState updated with new profile")
                } ?: run {
                    Log.w(TAG, "No user ID available to load profile")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile: ${e.stackTraceToString()}")
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