package com.example.project_we_fix_it.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun reportBreakdown(
        description: String,
        location: String,
        urgencyLevel: String,
        onSuccess: () -> Unit
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

                val breakdown = Breakdown(
                    breakdown_id = UUID.randomUUID().toString(),
                    reporter_id = currentUserId,
                    equipment_id = null,
                    urgency_level = dbUrgencyLevel,
                    location = location.trim(),
                    description = description.trim(),
                    status = "open",
                    reported_at = getCurrentDateTimeString(),
                    estimated_completion = null
                )

                supabaseRepository.createBreakdown(breakdown)
                _isSuccess.value = true
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to report breakdown: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun getCurrentDateTimeString(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
    }

    fun clearState() {
        _errorMessage.value = null
        _isSuccess.value = false
    }
}