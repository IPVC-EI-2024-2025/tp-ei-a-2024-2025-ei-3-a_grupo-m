package com.example.project_we_fix_it.supabase

import com.example.project_we_fix_it.BreakdownItem
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val user_id: String,
    val name: String,
    val role: String,
    val status: String = "active",
    val created_at: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val email: String? = null
)

@Serializable
data class Equipment(
    val equipment_id: String? = null,
    val identifier: String,
    val type: String,
    val model: String? = null,
    val location: String? = null,
    val status: String = "active"
)

@Serializable
data class Breakdown(
    val breakdown_id: String? = null,
    val reporter_id: String? = null,
    val equipment_id: String? = null,
    val urgency_level: String,
    val location: String? = null,
    val description: String,
    val status: String = "open",
    val reported_at: String? = null,
    val estimated_completion: String? = null
)

@Serializable
data class Assignment(
    val assignment_id: String? = null,
    val breakdown_id: String? = null,
    val technician_id: String? = null,
    val assigned_by: String? = null,
    val assigned_at: String? = null,
    val status: String = "active",
    val reassigned: Boolean = false
)

@Serializable
data class BreakdownPhoto(
    val photo_id: String? = null,
    val breakdown_id: String? = null,
    val photo_url: String,
    val uploaded_at: String? = null
)

@Serializable
data class BreakdownStatusHistory(
    val history_id: String? = null,
    val breakdown_id: String? = null,
    val status: String,
    val changed_by: String? = null,
    val changed_at: String? = null
)

@Serializable
data class TechnicianMetrics(
    val technician_id: String,
    val total_breakdowns_handled: Int = 0,
    val average_resolution_time: String? = null
)

@Serializable
data class Message(
    val message_id: String? = null,
    val chat_id: String? = null,
    val sender_id: String? = null,
    val receiver_id: String? = null,
    val breakdown_id: String? = null,
    val content: String,
    val sent_at: String? = null
)

@Serializable
data class BreakdownWithDetails(
    val breakdown_id: String,
    val reporter_id: String?,
    val equipment_id: String?,
    val urgency_level: String,
    val location: String?,
    val description: String,
    val status: String,
    val reported_at: String?,
    val estimated_completion: String?,
    val equipment: Equipment? = null,
    val reporter: UserProfile? = null,
    val photos: List<BreakdownPhoto> = emptyList(),
    val assignments: List<Assignment> = emptyList()
)

@Serializable
data class AssignmentWithDetails(
    val assignment_id: String,
    val breakdown_id: String?,
    val technician_id: String?,
    val assigned_by: String?,
    val assigned_at: String?,
    val status: String,
    val reassigned: Boolean,
    val breakdown: Breakdown? = null,
    val technician: UserProfile? = null,
    val assigner: UserProfile? = null
)

@Serializable
data class Chat(
    val chat_id: String? = null,
    val breakdown_id: String? = null,
    val created_at: String? = null,
    val last_message_at: String? = null,
    val lastMessagePreview: String? = null,
    val participants: List<String>
)

@Serializable
data class Notification(
    val notification_id: String? = null,
    val user_id: String,
    val title: String,
    val message: String,
    val related_id: String?,
    val read: Boolean = false,
    val created_at: String? = null,
    val metadata: String? = null
)

@Serializable
data class TechnicianActiveWork(
    val id: Long? = null,
    val breakdown_id: String,
    val technician_id: String,
    val started_at: String? = null
)

fun Breakdown.toBreakdownItem(): BreakdownItem {
    return BreakdownItem(
        id = breakdown_id ?: "",
        title = equipment_id ?: "Breakdown",
        description = description,
        priority = when (urgency_level) {
            "high" -> 1
            "medium" -> 2
            else -> 3
        }
    )
}