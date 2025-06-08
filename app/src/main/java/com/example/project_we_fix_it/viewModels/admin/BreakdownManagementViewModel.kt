package com.example.project_we_fix_it.viewModels.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakdownManagementViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {

    private val _breakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val breakdowns: StateFlow<List<Breakdown>> = _breakdowns.asStateFlow()

    private val _filteredBreakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val filteredBreakdowns: StateFlow<List<Breakdown>> = _filteredBreakdowns.asStateFlow()

    private val _technicians = MutableStateFlow<List<UserProfile>>(emptyList())
    val technicians: StateFlow<List<UserProfile>> = _technicians.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedBreakdown = MutableStateFlow<Breakdown?>(null)
    val selectedBreakdown: StateFlow<Breakdown?> = _selectedBreakdown.asStateFlow()

    private val _showTechnicianDialog = MutableStateFlow(false)
    val showTechnicianDialog: StateFlow<Boolean> = _showTechnicianDialog.asStateFlow()

    private val _filterStatus = MutableStateFlow("all")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    init {
        loadBreakdowns()
        loadTechnicians()
    }

    fun loadBreakdowns() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _breakdowns.value = supabaseRepository.getAllBreakdowns()
                applyFilter(_filterStatus.value)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTechnicians() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _technicians.value = supabaseRepository.getAllUsers()
                    .filter { it.role == "technician" && it.status == "active" }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBreakdown(breakdownId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.deleteBreakdown(breakdownId)
                loadBreakdowns()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBreakdown(breakdown: Breakdown) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.updateBreakdown(breakdown)
                loadBreakdowns()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun assignTechnician(breakdownId: String, technicianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val breakdown = _breakdowns.value.first { it.breakdown_id == breakdownId }
                val updatedBreakdown = breakdown.copy(status = "in_progress")
                supabaseRepository.updateBreakdown(updatedBreakdown)

                val assignment = Assignment(
                    breakdown_id = breakdownId,
                    technician_id = technicianId,
                    status = "active"
                )
                supabaseRepository.createAssignment(assignment)

                loadBreakdowns()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectBreakdown(breakdown: Breakdown) {
        _selectedBreakdown.value = breakdown
    }

    fun showTechnicianDialog(show: Boolean) {
        _showTechnicianDialog.value = show
    }

    fun applyFilter(status: String) {
        _filterStatus.value = status
        _filteredBreakdowns.value = when (status) {
            "all" -> _breakdowns.value
            else -> _breakdowns.value.filter { it.status == status }
        }
    }
}