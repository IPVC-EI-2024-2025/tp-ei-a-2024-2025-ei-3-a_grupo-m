package com.example.project_we_fix_it.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.supabase.Notification
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            try {
                val notifications = supabaseRepository.getUnreadNotifications(userId)
                _notifications.value = notifications
                _unreadCount.value = notifications.size
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun markAsRead(userId: String) {
        viewModelScope.launch {
            supabaseRepository.markNotificationsAsRead(userId)
            _unreadCount.value = 0
        }
    }
}