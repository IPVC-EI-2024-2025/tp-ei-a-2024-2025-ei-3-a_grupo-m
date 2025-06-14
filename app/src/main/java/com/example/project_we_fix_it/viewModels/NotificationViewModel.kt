package com.example.project_we_fix_it.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_we_fix_it.auth.AuthRepository
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
    private val supabaseRepository: SupabaseRepository,
    private val authRepository: AuthRepository,
    private val notificationService: NotificationService
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _newNotificationEvent = MutableStateFlow<Notification?>(null)
    val newNotificationEvent = _newNotificationEvent.asStateFlow()


    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("NotificationVM", "Loading notifications for user: $userId")
                val notifications = supabaseRepository.getNotificationsForUser(userId)
                Log.d("NotificationVM", "Retrieved ${notifications.size} notifications")

                _notifications.value = notifications
                _unreadCount.value = notifications.count { !it.read }

                notifications.take(3).forEach {
                    Log.d("NotificationVM", "Notification: ${it.title} - ${it.message} - Read: ${it.read}")
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "Error loading notifications", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                val success = supabaseRepository.markNotificationsAsRead(userId)
                if (success) {
                    _notifications.value = _notifications.value.map {
                        it.copy(read = true)
                    }
                    _unreadCount.value = 0
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "Error marking all as read", e)
            }
        }
    }

    fun deleteAllNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = supabaseRepository.deleteAllNotifications(userId)
                if (success) {
                    _notifications.value = emptyList()
                    _unreadCount.value = 0
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "Error deleting notifications", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleNewNotification(notification: Notification) {
        _notifications.value += notification
        if (!notification.read) {
            _unreadCount.value += 1
        }
        _newNotificationEvent.value = notification
    }

    fun resetNewNotificationEvent() {
        _newNotificationEvent.value = null
    }
}