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
    private val supabaseRepository: SupabaseRepository,
    private val notificationService: NotificationService
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

    private val _activelyWorkingOn = MutableStateFlow<Set<String>>(emptySet())
    val activelyWorkingOn: StateFlow<Set<String>> = _activelyWorkingOn.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        _assignments.value = emptyList()
        _workingOnBreakdowns.value = emptyList()
        _assignedBreakdowns.value = emptyList()
        _activelyWorkingOn.value = emptySet()
    }

    fun loadAssignments(technicianId: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        Log.d("AssignmentViewModel", "Starting to load assignments for technician: $technicianId")

        viewModelScope.launch {
            try {
                _assignments.value = emptyList()
                _workingOnBreakdowns.value = emptyList()
                _assignedBreakdowns.value = emptyList()
                _activelyWorkingOn.value = emptySet()

                val assignments = supabaseRepository.getAssignmentsByTechnician(technicianId)
                    .filter { it.status != "completed" }

                Log.d("AssignmentViewModel", "Found ${assignments.size} non-completed assignments")
                assignments.forEach { assignment ->
                    Log.d("AssignmentViewModel", "Assignment: ${assignment.assignment_id} - Status: ${assignment.status} - BreakdownId: ${assignment.breakdown_id}")
                }

                val activeBreakdowns = supabaseRepository.getActiveBreakdowns(technicianId)
                Log.d("AssignmentViewModel", "Active breakdowns: $activeBreakdowns")
                _activelyWorkingOn.value = activeBreakdowns.toSet()

                val assignmentsWithDetails = assignments.map { assignment ->
                    Log.d("AssignmentViewModel", "Processing assignment ID: ${assignment.assignment_id}")

                    val breakdown = assignment.breakdown_id?.let {
                        Log.d("AssignmentViewModel", "Fetching breakdown for ID: $it")
                        supabaseRepository.getBreakdownById(it).also { bd ->
                            Log.d("AssignmentViewModel", "Breakdown fetched: ${bd?.breakdown_id} - Status: ${bd?.status} - Description: ${bd?.description}")
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
                        Log.d("AssignmentViewModel", "Created AssignmentWithDetails: ${it.assignment_id} with breakdown: ${it.breakdown?.breakdown_id}")
                    }
                }

                _assignments.value = assignmentsWithDetails
                Log.d("AssignmentViewModel", "Set ${assignmentsWithDetails.size} assignments")

                val allAssigned = assignmentsWithDetails
                    .filter { assignment ->
                        val isValid = assignment.breakdown != null &&
                                assignment.technician_id == technicianId &&
                                assignment.breakdown.status != "closed" &&
                                assignment.breakdown.status != "completed"

                        Log.d("AssignmentViewModel", "Assignment ${assignment.assignment_id}: " +
                                "breakdown=${assignment.breakdown?.breakdown_id}, " +
                                "technicianMatch=${assignment.technician_id == technicianId}, " +
                                "status=${assignment.breakdown?.status}, " +
                                "isValid=$isValid")
                        isValid
                    }
                    .mapNotNull { it.breakdown }

                Log.d("AssignmentViewModel", "Filtered assigned breakdowns: ${allAssigned.size}")
                allAssigned.forEach { breakdown ->
                    Log.d("AssignmentViewModel", "Assigned breakdown: ${breakdown.breakdown_id} - ${breakdown.description} - Status: ${breakdown.status}")
                }

                _assignedBreakdowns.value = allAssigned

                val workingOn = allAssigned.filter { breakdown ->
                    activeBreakdowns.contains(breakdown.breakdown_id)
                }

                Log.d("AssignmentViewModel", "Working on breakdowns: ${workingOn.size}")
                workingOn.forEach { breakdown ->
                    Log.d("AssignmentViewModel", "Working on breakdown: ${breakdown.breakdown_id} - ${breakdown.description}")
                }

                _workingOnBreakdowns.value = workingOn

            } catch (e: Exception) {
                Log.e("AssignmentViewModel", "Error loading assignments", e)
                _errorMessage.value = "Failed to load assignments: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("AssignmentViewModel", "Finished loading assignments")
                Log.d("AssignmentViewModel", "Final state - Assigned: ${_assignedBreakdowns.value.size}, Working on: ${_workingOnBreakdowns.value.size}")
            }
        }
    }

    fun startWorkingOnBreakdown(breakdownId: String, technicianId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = supabaseRepository.markBreakdownAsActive(breakdownId, technicianId)

                if (success) {
                    Log.d("AssignmentViewModel", "Started Working On Breakdown")
                    loadAssignments(technicianId)
                } else {
                    _errorMessage.value = "Failed to mark breakdown as active"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestCompleteBreakdown(breakdownId: String, technicianId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("AssignmentViewModel", "Initiating complete request for $breakdownId")
                notificationService.notifyCompleteRequest(
                    breakdownId = breakdownId,
                    technicianId = technicianId
                )
                Log.d("AssignmentViewModel", "Notification service called/made request to complete")
                loadAssignments(technicianId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to request completion: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshAssignments(technicianId: String) {
        Log.d("AssignmentViewModel", "Refreshing assignments - clearing current data")
        _assignments.value = emptyList()
        _workingOnBreakdowns.value = emptyList()
        _assignedBreakdowns.value = emptyList()
        _activelyWorkingOn.value = emptySet()
        _errorMessage.value = ""

        loadAssignments(technicianId)
    }
}