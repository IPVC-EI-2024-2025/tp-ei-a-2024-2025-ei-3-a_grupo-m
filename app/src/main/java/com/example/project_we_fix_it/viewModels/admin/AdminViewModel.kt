package com.example.project_we_fix_it.viewModels.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthViewModel
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.Equipment
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _equipment = MutableStateFlow<List<Equipment>>(emptyList())
    val equipment: StateFlow<List<Equipment>> = _equipment.asStateFlow()

    private val _breakdowns = MutableStateFlow<List<Breakdown>>(emptyList())
    val breakdowns: StateFlow<List<Breakdown>> = _breakdowns.asStateFlow()

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
            } catch (e: Exception) {
                _error.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    //User
    fun createUser(user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.updateUserProfile(user)
                loadAllData() // Refresh the list
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

                // Show success message
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
            try {
                val createdEquipment = supabaseRepository.createEquipment(equipment)
                _equipment.value += createdEquipment
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    Log.d("AdminEquipment", "Supabase returned array error, but creating local state anyway")
                } else {
                    Log.d("AdminEquipment", "Equipment create failed: ${e.stackTraceToString()}")
                    _error.value = "Failed to create equipment: ${e.message}"
                }
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

    //Breakdowns

    fun createBreakdown(breakdown: Breakdown) {
        viewModelScope.launch {
            try {
                supabaseRepository.createBreakdown(breakdown)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error creating breakdown: ${e.message}")
            }
        }
    }

    fun updateBreakdown(breakdown: Breakdown) {
        viewModelScope.launch {
            try {
                supabaseRepository.updateBreakdown(breakdown)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error updating breakdown: ${e.message}")
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

    //Assignments

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            try {
                supabaseRepository.createAssignment(assignment)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error creating assignment: ${e.message}")
            }
        }
    }

    fun updateAssignment(assignmentId: String, status: String){
        viewModelScope.launch {
            try {
                supabaseRepository.updateAssignmentStatus(assignmentId, status)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error updating assignment: ${e.message}")
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            try {
                supabaseRepository.deleteAssignment(assignmentId)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error deleting assignment: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                supabaseRepository.deleteUser(userId)
                loadAllData()
            } catch (e: Exception) {
                throw Exception("Error deleting user: ${e.message}")
            }
        }
    }

}