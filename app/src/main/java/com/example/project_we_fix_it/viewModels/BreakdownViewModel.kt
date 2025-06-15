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
class BreakdownViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {

    private val _breakdown = MutableStateFlow<BreakdownWithDetails?>(null)
    val breakdown: StateFlow<BreakdownWithDetails?> = _breakdown.asStateFlow()

    private val _photos = MutableStateFlow<List<BreakdownPhoto>>(emptyList())
    val photos: StateFlow<List<BreakdownPhoto>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBreakdownDetails(breakdownId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get breakdown
                val breakdown = supabaseRepository.getBreakdownById(breakdownId) ?: return@launch

                // Get related data
                val equipment = breakdown.equipment_id?.let {
                    supabaseRepository.getEquipmentById(it)
                }

                val assignments = supabaseRepository.getAllAssignments()
                    .filter { it.breakdown_id == breakdownId }

                // Get photos with fresh URLs
                val photos = supabaseRepository.getBreakdownPhotosWithUrls(breakdownId)
                _photos.value = photos

                // Combine into BreakdownWithDetails
                _breakdown.value = BreakdownWithDetails(
                    breakdown_id = breakdownId,
                    reporter_id = breakdown.reporter_id,
                    equipment_id = breakdown.equipment_id,
                    urgency_level = breakdown.urgency_level,
                    location = breakdown.location,
                    description = breakdown.description,
                    status = breakdown.status,
                    reported_at = breakdown.reported_at,
                    estimated_completion = breakdown.estimated_completion,
                    equipment = equipment,
                    photos = photos,
                    assignments = assignments
                )
            } catch (e: Exception) {
                _error.value = "Failed to load breakdown details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadPhoto(breakdownId: String, imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val photo = supabaseRepository.uploadBreakdownPhoto(breakdownId, imageBytes, fileName)
                _photos.value += photo
                // Refresh breakdown details to include the new photo
                loadBreakdownDetails(breakdownId)
            } catch (e: Exception) {
                _error.value = "Failed to upload photo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePhoto(photoId: String, filePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = supabaseRepository.deleteBreakdownPhoto(photoId, filePath)
                if (success) {
                    _photos.value = _photos.value.filter { it.photo_id != photoId }
                    // Refresh breakdown details
                    _breakdown.value?.breakdown_id?.let { loadBreakdownDetails(it) }
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete photo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBreakdownStatus(breakdownId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.updateBreakdownStatus(breakdownId, status)
                loadBreakdownDetails(breakdownId)
            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBreakdownUrgencyLevel(breakdownId: String, urgency: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                supabaseRepository.updateBreakdownUrgency(breakdownId, urgency)
                loadBreakdownDetails(breakdownId)
            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getTechnicianName(technicianId: String): String? {
        return supabaseRepository.getUserProfile(technicianId)?.name
    }
}