package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBreakdownsViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myBreakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val myBreakdowns: StateFlow<List<Breakdown>> = _myBreakdowns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMyBreakdowns() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = authRepository.getCurrentUser()?.id
                Log.d("MyBreakdownsVM", "Current user ID: $currentUserId")

                if (currentUserId != null) {
                    val breakdowns = repository.getBreakdownsByReporter(currentUserId)
                    Log.d("MyBreakdownsVM", "Fetched breakdowns: ${breakdowns.size}")

                    _myBreakdowns.value = breakdowns.sortedByDescending { it.reported_at }
                    Log.d("MyBreakdownsVM", "Breakdowns after sorting: ${_myBreakdowns.value.size}")
                } else {
                    Log.d("MyBreakdownsVM", "No current user ID found")
                }
            } catch (e: Exception) {
                Log.e("MyBreakdownsVM", "Error loading breakdowns", e)
                _error.value = "Failed to load breakdowns: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}