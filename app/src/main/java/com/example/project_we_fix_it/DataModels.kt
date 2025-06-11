package com.example.project_we_fix_it

data class BreakdownItem(
    val id: String,
    val title: String,
    val description: String,
    val priority: Int = 1
)
