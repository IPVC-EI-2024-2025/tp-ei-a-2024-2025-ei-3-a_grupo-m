package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.BreakdownPhoto
import com.example.project_we_fix_it.supabase.Equipment
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

    private val _equipment = MutableStateFlow<List<Equipment>>(emptyList())
    val equipment: StateFlow<List<Equipment>> = _equipment.asStateFlow()

    private val _breakdownPhotos = MutableStateFlow<Map<String, List<BreakdownPhoto>>>(emptyMap())
    val breakdownPhotos: StateFlow<Map<String, List<BreakdownPhoto>>> = _breakdownPhotos.asStateFlow()

    init {
        loadBreakdowns()
        loadEquipment()
    }

    private fun loadEquipment() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _equipment.value = supabaseRepository.getAllEquipment()
            } catch (e: Exception) {
                _error.value = "Failed to load equipment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
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

    fun createBreakdown(breakdown: Breakdown, photos: List<ByteArray>?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val createdBreakdown = supabaseRepository.createBreakdown(breakdown)
                _breakdowns.value += createdBreakdown

                photos?.let { photoList ->
                    createdBreakdown.breakdown_id?.let { breakdownId ->
                        val uploadedPhotos = mutableListOf<BreakdownPhoto>()

                        photoList.forEach { photoBytes ->
                            try {
                                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                                val photo = supabaseRepository.uploadBreakdownPhoto(
                                    breakdownId = breakdownId,
                                    imageBytes = photoBytes,
                                    fileName = fileName
                                )
                                uploadedPhotos.add(photo)
                            } catch (e: Exception) {
                                Log.e("DashboardVM", "Error uploading photo", e)
                            }
                        }

                        if (uploadedPhotos.isNotEmpty()) {
                            _breakdownPhotos.value = _breakdownPhotos.value.toMutableMap().apply {
                                put(breakdownId, uploadedPhotos)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e.message?.contains("Expected start of the array") == true) {
                    _breakdowns.value += breakdown
                    Log.d("DashboardVM", "Supabase returned array error, but created breakdown anyway")
                } else {
                    _error.value = "Failed to create breakdown: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

}