package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val notificationService: NotificationService,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val TAG = "ProfileVM"

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun loadProfile(userId: String) {
        Log.d(TAG, "loadProfile() called with: $userId")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading profile...")
                val profile = supabaseRepository.getUserProfile(userId)
                Log.d(TAG, "Profile loaded: $profile")
                _profileState.value = profile
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load profile: ${e.message}")
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        Log.d(TAG, "updateProfile() called with: ${profile.name}")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Starting profile update...")
                val currentUserId = authRepository.getCurrentUser()?.id ?: ""
                val updatedProfile = supabaseRepository.updateUserProfile(profile)

                Log.d(TAG, "Profile update successful, updating state")
                _profileState.value = updatedProfile
                _updateSuccess.value = true

                // Send notification about profile update
                notificationService.notifyUserProfileChange(
                    userProfile = updatedProfile,
                    operation = "updated",
                    currentUserId = currentUserId
                )

                Log.d(TAG, "State updated successfully")
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.w(TAG, "Supabase returned array error, but updating local state anyway")
                    _profileState.value = profile
                    _updateSuccess.value = true

                    // Even if Supabase returned an array error, we'll try to send notification
                    val currentUserId = authRepository.getCurrentUser()?.id ?: ""
                    notificationService.notifyUserProfileChange(
                        userProfile = profile,
                        operation = "updated",
                        currentUserId = currentUserId
                    )
                } else {
                    Log.e(TAG, "Profile update failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to update profile: ${e.message}"
                }
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Profile update flow completed")
            }
        }
    }

    fun clearError() {
        Log.d(TAG, "Clearing error state")
        _error.value = null
        _updateSuccess.value = false
    }
}