package com.example.project_we_fix_it

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _breakdowns = MutableStateFlow<List<BreakdownItem>>(emptyList())
    val breakdowns: StateFlow<List<BreakdownItem>> = _breakdowns

    init {
        loadBreakdowns()
    }

    private fun loadBreakdowns() {
        viewModelScope.launch {
            _breakdowns.value = listOf(
                BreakdownItem(
                    id = "1",
                    title = "Computer not working",
                    description = "The computer won't turn on",
                    priority = 2
                ),
                BreakdownItem(
                    id = "2",
                    title = "Printer issue",
                    description = "Paper jam in the printer",
                    priority = 1
                )
            )
        }
    }
}