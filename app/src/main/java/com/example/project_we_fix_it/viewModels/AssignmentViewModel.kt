package com.example.project_we_fix_it.viewModels

import android.util.Log
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
        Log.d("AssignmentViewModel", "Starting to load assignments for technician: $technicianId")

        viewModelScope.launch {
            try {
                // Fetch assignments for the technician
                Log.d("AssignmentViewModel", "Fetching assignments from repository...")
                val assignments = supabaseRepository.getAssignmentsByTechnician(technicianId)
                Log.d("AssignmentViewModel", "Raw assignments received: ${assignments.size} items")
                assignments.forEach { assignment ->
                    Log.d("AssignmentViewModel", "Assignment: $assignment")
                }

                val allAssignments = supabaseRepository.getAllAssignmentsDebug()
                Log.d("AssignmentViewModel", "All assignments received: ${allAssignments.size} items")
                allAssignments.forEach { assignment ->
                    Log.d("AssignmentViewModel", "All Assignment: $assignment")
                }

                // Convert to AssignmentWithDetails by fetching related breakdowns
                val assignmentsWithDetails = assignments.map { assignment ->
                    Log.d("AssignmentViewModel", "Processing assignment ID: ${assignment.assignment_id}")

                    val breakdown = assignment.breakdown_id?.let {
                        Log.d("AssignmentViewModel", "Fetching breakdown for ID: $it")
                        supabaseRepository.getBreakdownById(it).also { bd ->
                            Log.d("AssignmentViewModel", "Breakdown fetched: ${bd?.breakdown_id}")
                        }
                    }

                    val technician = assignment.technician_id?.let {
                        Log.d("AssignmentViewModel", "Fetching technician profile for ID: $it")
                        supabaseRepository.getUserProfile(it).also { tech ->
                            Log.d("AssignmentViewModel", "Technician profile fetched: ${tech?.name}")
                        }
                    }

                    val assigner = assignment.assigned_by?.let {
                        Log.d("AssignmentViewModel", "Fetching assigner profile for ID: $it")
                        supabaseRepository.getUserProfile(it).also { assign ->
                            Log.d("AssignmentViewModel", "Assigner profile fetched: ${assign?.name}")
                        }
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
                    ).also {
                        Log.d("AssignmentViewModel", "Created AssignmentWithDetails: $it")
                    }
                }

                _assignments.value = assignmentsWithDetails
                Log.d("AssignmentViewModel", "Assignments with details set: ${assignmentsWithDetails.size} items")

                val workingOn = assignmentsWithDetails
                    .filter { it.breakdown?.status == "in_progress" }
                    .mapNotNull { it.breakdown }
                Log.d("AssignmentViewModel", "Working on breakdowns: ${workingOn.size} items")

                val assigned = assignmentsWithDetails
                    .filter {
                        it.breakdown != null &&
                                it.technician_id == technicianId &&
                                it.breakdown.status != "closed"
                    }
                    .mapNotNull { it.breakdown }
                Log.d("AssignmentViewModel", "Assigned breakdowns: ${assigned.size} items")

                _workingOnBreakdowns.value = workingOn
                _assignedBreakdowns.value = assigned

            } catch (e: Exception) {
                Log.e("AssignmentViewModel", "Error loading assignments", e)
                _errorMessage.value = "Failed to load assignments: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("AssignmentViewModel", "Finished loading assignments")
            }
        }
    }
}