package com.example.project_we_fix_it.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {

    private val _assignments = MutableStateFlow<List<AssignmentWithDetails>>(emptyList())
    val assignments: StateFlow<List<AssignmentWithDetails>> = _assignments.asStateFlow()

    private val _workingOnBreakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val workingOnBreakdowns: StateFlow<List<Breakdown>> = _workingOnBreakdowns.asStateFlow()

    private val _assignedBreakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val assignedBreakdowns: StateFlow<List<Breakdown>> = _assignedBreakdowns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadAssignments(technicianId: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                // Fetch assignments for the technician
                val assignments = supabaseRepository.getAssignmentsByTechnician(technicianId)

                // Convert to AssignmentWithDetails by fetching related breakdowns
                val assignmentsWithDetails = assignments.map { assignment ->
                    val breakdown = assignment.breakdown_id?.let {
                        supabaseRepository.getBreakdownById(it)
                    }
                    val technician = assignment.technician_id?.let {
                        supabaseRepository.getUserProfile(it)
                    }
                    val assigner = assignment.assigned_by?.let {
                        supabaseRepository.getUserProfile(it)
                    }

                    AssignmentWithDetails(
                        assignment_id = assignment.assignment_id ?: "",
                        breakdown_id = assignment.breakdown_id,
                        technician_id = assignment.technician_id,
                        assigned_by = assignment.assigned_by,
                        assigned_at = assignment.assigned_at,
                        status = assignment.status,
                        reassigned = assignment.reassigned,
                        breakdown = breakdown,
                        technician = technician,
                        assigner = assigner
                    )
                }

                _assignments.value = assignmentsWithDetails

                // Separate breakdowns by status
                val workingOn = assignmentsWithDetails
                    .filter { it.breakdown?.status == "in_progress" }
                    .mapNotNull { it.breakdown }

                val assigned = assignmentsWithDetails
                    .filter { it.breakdown?.status == "open" }
                    .mapNotNull { it.breakdown }

                _workingOnBreakdowns.value = workingOn
                _assignedBreakdowns.value = assigned

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load assignments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBreakdownStatus(breakdownId: String, status: String, technicianId: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val updatedBreakdown = supabaseRepository.updateBreakdownStatus(breakdownId, status)
                // Refresh the data after update
                loadAssignments(technicianId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update breakdown status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}