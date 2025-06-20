package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BreakdownReportingViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _photosToUpload = MutableStateFlow<List<ByteArray>>(emptyList())
    val photosToUpload: StateFlow<List<ByteArray>> = _photosToUpload.asStateFlow()

    private val notificationService: NotificationService = NotificationService(supabaseRepository, authRepository)

    fun addPhotoToUpload(imageBytes: ByteArray) {
        _photosToUpload.value += imageBytes
    }

    fun removePhotoToUpload(index: Int) {
        _photosToUpload.value = _photosToUpload.value.toMutableList().apply { removeAt(index) }
    }

    fun reportBreakdown(
        description: String,
        location: String,
        urgencyLevel: String,
        onSuccess: (breakdownId: String) -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = false

        viewModelScope.launch {
            try {
                if (description.isBlank()) {
                    throw Exception("Description cannot be empty")
                }

                val currentUserId = authRepository.getCurrentUser()?.id
                    ?: throw Exception("User not authenticated")
                val dbUrgencyLevel = when (urgencyLevel.lowercase()) {
                    "low" -> "low"
                    "normal" -> "normal"
                    "high" -> "high"
                    "critical" -> "critical"
                    else -> "normal"
                }

                Log.d("BreakdownsReporting", "Breakdown reported by: $currentUserId")
                val breakdown = Breakdown(
                    breakdown_id = null,
                    reporter_id = currentUserId,
                    equipment_id = null,
                    urgency_level = dbUrgencyLevel,
                    location = location.trim(),
                    description = description.trim(),
                    status = "open",
                    reported_at = getCurrentDateTimeString(),
                    estimated_completion = null
                )

                val createdBreakdown = supabaseRepository.createBreakdown(breakdown)
                val breakdownId = createdBreakdown.breakdown_id ?: throw Exception("Failed to get breakdown ID")

                _photosToUpload.value.forEachIndexed { index, imageBytes ->
                    supabaseRepository.uploadBreakdownPhoto(
                        breakdownId = breakdownId,
                        imageBytes = imageBytes,
                        fileName = "photo_$index.jpg"
                    )
                }

                _isSuccess.value = true
                onSuccess(breakdownId)
                notificationService.notifyBreakdownChange(
                    breakdown = createdBreakdown,
                    operation = "created",
                    currentUserId = currentUserId
                )

                // Clear photos after successful upload
                _photosToUpload.value = emptyList()
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("BreakdownReporting", "Supabase returned array error, but creating local state anyway")
                } else {
                    Log.d("BreakdownReporting", "Breakdown create failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to create breakdown: ${e.message}"
                }
            } finally {
                _isLoading.value = false
                Log.d("BreakdownReporting", "Breakdown creation flow completed")
            }
        }
    }

    private fun getCurrentDateTimeString(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
    }

}