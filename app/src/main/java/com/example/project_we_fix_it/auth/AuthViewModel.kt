package com.example.project_we_fix_it.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Initialize with loading state
    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val isLoggedIn = authRepository.isUserLoggedIn()
                val user = authRepository.getCurrentUser()
                val userProfile = if (isLoggedIn) authRepository.getCurrentUserProfile() else null

                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = isLoggedIn,
                    user = user,
                    userProfile = userProfile,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = AuthState(
                    isLoading = false,
                    isLoggedIn = false,
                    error = e.message ?: "Unknown error"
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

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}