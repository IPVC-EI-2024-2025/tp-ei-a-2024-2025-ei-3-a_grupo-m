package com.example.project_we_fix_it.viewModels.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.Equipment
import com.example.project_we_fix_it.supabase.Notification
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import com.example.project_we_fix_it.viewModels.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val notificationService: NotificationService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _equipment = MutableStateFlow<List<Equipment>>(emptyList())
    val equipment: StateFlow<List<Equipment>> = _equipment.asStateFlow()

    private val _breakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val breakdowns: StateFlow<List<Breakdown>> = _breakdowns.asStateFlow()

    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _users.value = supabaseRepository.getAllUsers()
                _equipment.value = supabaseRepository.getAllEquipment()
                _breakdowns.value = supabaseRepository.getAllBreakdowns()
                _assignments.value = supabaseRepository.getAllAssignments()
            } catch (e: Exception) {
                _error.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createUser(user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.updateUserProfile(user)
                notificationService.notifyUserProfileChange(
                    userProfile = user,
                    operation = "created",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                loadAllData()
            } catch (e: Exception) {
                _error.value = "Failed to create user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateUser(user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedProfile = supabaseRepository.updateUserProfile(user)
                _users.value = _users.value.map {
                    if (it.user_id == user.user_id) updatedProfile else it
                }

                _error.value = "Profile updated successfully. " +
                        if (user.email != null) "Email change confirmation sent." else ""

            } catch (e: Exception) {
                _error.value = when {
                    e.message?.contains("Email update") == true ->
                        "Profile updated but email change failed: ${e.message}"
                    else -> "Update failed: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Equipment

    fun createEquipment(equipment: Equipment) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("AdminEquipment", "Starting equipment creation flow")
            try {
                Log.d("AdminEquipment", "Creating equipment: ${equipment.identifier}")
                val currentUserId = authRepository.getCurrentUser()?.id ?: "unknown"
                Log.d("AdminEquipment", "Current user ID: $currentUserId")

                notificationService.notifyEquipmentChange(
                    equipment = equipment,
                    operation = "created",
                    currentUserId = currentUserId
                )
                Log.d("AdminEquipment", "Notification sent successfully")

                val createdEquipment = try {
                    supabaseRepository.createEquipment(equipment)
                } catch (e: Exception) {
                    Log.e("AdminEquipment", "Supabase create failed, but notification sent", e)
                    equipment
                }

                Log.d("AdminEquipment", "Equipment created in Supabase: ${createdEquipment.equipment_id}")
                _equipment.value += createdEquipment
                Log.d("AdminEquipment", "Local state updated with new equipment")

            } catch (e: Exception) {
                Log.e("AdminEquipment", "Equipment create failed", e)
                _error.value = "Failed to create equipment: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("AdminEquipment", "Equipment creation flow completed")
            }
        }
    }

    fun updateEquipment(equipment: Equipment) {
        viewModelScope.launch {
            try {
                supabaseRepository.updateEquipment(equipment)
                notificationService.notifyEquipmentChange(
                    equipment = equipment,
                    operation = "updated",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                loadAllData()
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminEquipment", "Supabase returned array error, but updating local state anyway")
                } else {
                    Log.d("AdminEquipment", "Equipment update failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to update equipment: ${e.message}"
                }
            } finally {
                _isLoading.value = false
                Log.d("AdminEquipment", "Equipment update flow completed")
            }
        }
    }

    fun deleteEquipment(equipmentId: String) {
        viewModelScope.launch {
            try {
                supabaseRepository.deleteEquipment(equipmentId)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error deleting equipment: ${e.message}")
            }
        }
    }

    fun createBreakdown(breakdown: Breakdown) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val createdBreakdown = supabaseRepository.createBreakdown(breakdown)
                _breakdowns.value += createdBreakdown
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminBreakdown", "Supabase returned array error, but creating local state anyway")
                } else {
                    Log.d("AdminBreakdown", "Breakdown create failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to create breakdown: ${e.message}"
                }
            } finally {
                _isLoading.value = false
                notificationService.notifyBreakdownChange(
                    breakdown = breakdown,
                    operation = "created",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                Log.d("AdminEquipment", "Breakdown creation flow completed")
            }
        }
    }

    fun updateBreakdown(breakdown: Breakdown) {
        viewModelScope.launch {
            try {
                supabaseRepository.updateBreakdown(breakdown)
                loadAllData()
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminBreakdown", "Supabase returned array error, but updating local state anyway")
                } else {
                    Log.d("AdminBreakdown", "Equipment update failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to update Breakdown: ${e.message}"
                }
            } finally {
                notificationService.notifyBreakdownChange(
                    breakdown = breakdown,
                    operation = "updated",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                _isLoading.value = false
                Log.d("AdminBreakdown", "Breakdown update flow completed")
            }
        }
    }

    fun deleteBreakdown(breakdownId: String) {
        viewModelScope.launch {
            try {
                supabaseRepository.deleteBreakdown(breakdownId)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error deleting breakdown: ${e.message}")
            }
        }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val technicianExists = users.value.any { it.user_id == assignment.technician_id }
                if (!technicianExists) {
                    _error.value = "Selected technician does not exist"
                    return@launch
                }

                val createdAssignment = supabaseRepository.createAssignment(assignment)
                notificationService.notifyAssignmentChange(
                    assignment = createdAssignment,
                    operation = "created",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                _assignments.value += createdAssignment
            } catch (e: Exception) {
                if (e.message?.contains("foreign key constraint") == true) {
                    _error.value = "Cannot create assignment: Selected technician is invalid"
                }
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminAssignment", "Supabase returned array error, but creating local state anyway")
                } else {
                    Log.d("AdminAssignment", "Assignment create failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to create assignment: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAssignment(assignmentId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedAssignment = supabaseRepository.updateAssignmentStatus(assignmentId, status)
                notificationService.notifyAssignmentChange(
                    assignment = updatedAssignment,
                    operation = "updated",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )
                loadAllData()
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminAssignment", "Supabase returned array error, but updating local state anyway")
                } else {
                    Log.d("AdminAssignment", "Assignment update failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to update assignment: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            try {
                // Get assignment before deleting
                val assignment = assignments.value.firstOrNull { it.assignment_id == assignmentId }
                supabaseRepository.deleteAssignment(assignmentId)

                assignment?.let {
                    notificationService.notifyAssignmentChange(
                        assignment = it,
                        operation = "deleted",
                        currentUserId = authRepository.getCurrentUser()?.id ?: ""
                    )
                }

                loadAllData()
            } catch (e: Exception) {
                _error.value = "Error deleting assignment: ${e.message}"
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                val user = supabaseRepository.getUserProfile(userId)
                supabaseRepository.deleteUser(userId)

                user?.let {
                    notificationService.notifyUserProfileChange(
                        userProfile = it,
                        operation = "deleted",
                        currentUserId = authRepository.getCurrentUser()?.id ?: ""
                    )
                }

                loadAllData()
            } catch (e: Exception) {
                _error.value = "Error deleting user: ${e.message}"
            }
        }
    }

    fun completeBreakdown(breakdownId: String, technicianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedBreakdown = supabaseRepository.updateBreakdownStatus(breakdownId, "completed")

                _assignments.value.filter { it.breakdown_id == breakdownId }
                    .forEach { assignment ->
                        assignment.assignment_id?.let { id ->
                            supabaseRepository.updateAssignmentStatus(id, "completed")
                        }
                    }

                supabaseRepository.removeFromActiveWork(breakdownId, technicianId)

                loadAllData()

                notificationService.notifyBreakdownChange(
                    breakdown = updatedBreakdown,
                    operation = "updated",
                    currentUserId = authRepository.getCurrentUser()?.id ?: ""
                )

            } catch (e: Exception) {
                _error.value = "Failed to complete breakdown: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}