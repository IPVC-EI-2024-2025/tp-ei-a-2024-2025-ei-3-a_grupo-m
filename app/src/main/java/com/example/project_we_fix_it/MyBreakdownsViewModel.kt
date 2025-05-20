package com.example.project_we_fix_it

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBreakdownsViewModel @Inject constructor(
    // Repos here
    // private val breakdownRepository: BreakdownRepository
) : ViewModel() {

    private val _myBreakdowns = MutableStateFlow<List<BreakdownItem>>(emptyList())
    val myBreakdowns: StateFlow<List<BreakdownItem>> = _myBreakdowns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMyBreakdowns() {
        viewModelScope.launch {
            _isLoading.value = true

            // fetch from repo
            // val result = breakdownRepository.getMyBreakdowns()

            delay(1000)

            // Mock data - until i start with bd
            val mockBreakdowns = listOf(
                BreakdownItem(
                    id = "1",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "2",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "3",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "4",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "5",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "6",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                ),
                BreakdownItem(
                    id = "7",
                    title = "Breakdown 3",
                    description = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
                    priority = 2
                )
            )

            _myBreakdowns.value = mockBreakdowns
            _isLoading.value = false
        }
    }
}