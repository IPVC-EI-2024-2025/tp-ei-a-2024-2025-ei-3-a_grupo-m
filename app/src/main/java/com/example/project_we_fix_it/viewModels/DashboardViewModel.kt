package com.example.project_we_fix_it.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.Breakdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {
    private val _breakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val breakdowns: StateFlow<List<Breakdown>> = _breakdowns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBreakdowns()
    }

    fun loadBreakdowns() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _breakdowns.value = supabaseRepository.getAllBreakdowns()
                    .filter { it.status != "completed" }
            } catch (e: Exception) {
                _error.value = "Failed to load breakdowns: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshBreakdowns() {
        loadBreakdowns()
    }
}