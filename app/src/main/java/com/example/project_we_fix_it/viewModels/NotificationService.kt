package com.example.project_we_fix_it.viewModels

import android.util.Log
import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.supabase.Assignment
import com.example.project_we_fix_it.supabase.Breakdown
import com.example.project_we_fix_it.supabase.Chat
import com.example.project_we_fix_it.supabase.Equipment
import com.example.project_we_fix_it.supabase.Message
import com.example.project_we_fix_it.supabase.Notification
import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json

class NotificationService @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val authRepository: AuthRepository
) {

    private suspend fun createNotification(
        userId: String,
        title: String,
        message: String,
        relatedId: String? = null,
        metadata: String? = null
    ) {
        try {
            Log.d("NotificationService", "Creating notification for user $userId")
            Log.d("NotificationService", "Title: $title, Message: $message")

            val notification = Notification(
                user_id = userId,
                title = title,
                message = message,
                related_id = relatedId,
                metadata = metadata,
                read = false,
                created_at = null,
                notification_id = null
            )

            Log.d("NotificationService", "Notification object created, attempting to insert")
            val result = supabaseRepository.createNotification(notification)
            Log.d("NotificationService", "Notification created successfully: $result")
        } catch (e: Exception) {
            Log.e("NotificationService", "Failed to create notification", e)
        }
    }

    private suspend fun notifyCrudOperation(
        entityType: String,
        operation: String,
        entityId: String?,
        entityName: String? = null,
        currentUserId: String,
        affectedUserIds: List<String>,
        additionalMetadata: Map<String, String> = emptyMap()
    ) {
        val currentUserName = supabaseRepository.getUserProfile(currentUserId)?.name ?: "System"

        if (entityType == "assignment") {
            val (title, message) = when (operation) {
                "created" -> {
                    if (additionalMetadata["technician_id"] in affectedUserIds) {
                        "New Assignment" to "You were assigned to this breakdown"
                    } else {
                        "Assignment Created" to "Technician assigned to breakdown"
                    }
                }
                "updated" -> {
                    if (additionalMetadata["technician_id"] in affectedUserIds) {
                        "Assignment Updated" to "Your assignment was updated"
                    } else {
                        "Assignment Updated" to "Assignment was modified"
                    }
                }
                "deleted" -> {
                    if (additionalMetadata["technician_id"] in affectedUserIds) {
                        "Assignment Removed" to "You were removed from this assignment"
                    } else {
                        "Assignment Removed" to "Assignment was removed"
                    }
                }
                else -> {
                    "Assignment Change" to "Assignment status changed"
                }
            }

            val metadata = buildMap {
                put("operation", operation)
                put("by", currentUserName) // Sempre o nome, nunca ID
                putAll(additionalMetadata.filter { it.key != "by" })
            }.let { Json.encodeToString(it) }

            affectedUserIds.forEach { userId ->
                createNotification(
                    userId = userId,
                    title = title,
                    message = message,
                    relatedId = entityId,
                    metadata = metadata
                )
            }
            return
        }

        val title = when (operation) {
            "created" -> "New $entityType"
            "updated" -> "$entityType Updated"
            "deleted" -> "$entityType Deleted"
            else -> "$entityType Change"
        }.replaceFirstChar { it.uppercase() }

        val message = when (operation) {
            "created" -> "New $entityType${entityName?.let { ": $it" } ?: ""} was created"
            "updated" -> "$entityType${entityName?.let { " $it" } ?: ""} was updated"
            "deleted" -> "$entityType${entityName?.let { " $it" } ?: ""} was deleted"
            else -> "$entityType${entityName?.let { " $it" } ?: ""} was modified"
        }

        val metadata = buildMap {
            put("operation", operation)
            put("by", currentUserName)
            putAll(additionalMetadata)
        }.let { Json.encodeToString(it) }

        affectedUserIds.forEach { userId ->
            createNotification(
                userId = userId,
                title = title,
                message = message,
                relatedId = entityId,
                metadata = metadata
            )
        }
    }

    suspend fun notifyEquipmentChange(
        equipment: Equipment,
        operation: String,
        currentUserId: String
    ) {
        Log.d("NotificationService", "Starting equipment notification for ${equipment.equipment_id}")
        try {
            val affectedUserIds = when (operation) {
                "deleted" -> {
                    Log.d("NotificationService", "Getting users to notify for deletion")
                    val admins = supabaseRepository.getUserIdsByRole("admin")
                    val managers = supabaseRepository.getUserIdsByRole("manager")
                    Log.d("NotificationService", "Found ${admins.size} admins and ${managers.size} managers")
                    admins + managers
                }
                else -> {
                    Log.d("NotificationService", "Default notification to current user only")
                    listOf(currentUserId)
                }
            }.distinct()

            Log.d("NotificationService", "Notifying ${affectedUserIds.size} users about equipment $operation")

            notifyCrudOperation(
                entityType = "equipment",
                operation = operation,
                entityId = equipment.equipment_id,
                entityName = equipment.identifier,
                currentUserId = currentUserId,
                affectedUserIds = affectedUserIds,
                additionalMetadata = mapOf("type" to equipment.type)
            )
            Log.d("NotificationService", "Equipment notification completed successfully")
        } catch (e: Exception) {
            Log.e("NotificationService", "Failed to send equipment notification", e)
        }
    }

    suspend fun notifyBreakdownChange(
        breakdown: Breakdown,
        operation: String,
        currentUserId: String
    ) {
        val affectedUserIds = buildList {
            breakdown.reporter_id?.let { add(it) }

            if (operation != "created" && breakdown.breakdown_id != null) {
                addAll(
                    supabaseRepository.getAllAssignments()
                        .filter { it.breakdown_id == breakdown.breakdown_id }
                        .mapNotNull { it.technician_id }
                )
            }

            if (operation == "deleted") {
                addAll(supabaseRepository.getUserIdsByRole("admin"))
            }
        }.distinct()

        notifyCrudOperation(
            entityType = "breakdown",
            operation = operation,
            entityId = breakdown.breakdown_id,
            entityName = breakdown.description.take(30) + "...",
            currentUserId = currentUserId,
            affectedUserIds = affectedUserIds,
            additionalMetadata = mapOf(
                "status" to breakdown.status,
                "urgency" to breakdown.urgency_level
            )
        )
    }

    suspend fun notifyAssignmentChange(
        assignment: Assignment,
        operation: String,
        currentUserId: String
    ) {
        val affectedUserIds = buildList {
            assignment.technician_id?.let { add(it) }
            assignment.assigned_by?.takeIf { it != currentUserId }?.let { add(it) }
            if (operation == "deleted") addAll(supabaseRepository.getUserIdsByRole("admin"))
        }.distinct()

        notifyCrudOperation(
            entityType = "assignment",
            operation = operation,
            entityId = assignment.assignment_id,
            currentUserId = currentUserId,
            affectedUserIds = affectedUserIds,
            additionalMetadata = mapOf(
                "status" to assignment.status,
                "breakdown_id" to (assignment.breakdown_id ?: ""),
                "technician_id" to (assignment.technician_id ?: "")
            )
        )
    }

    suspend fun notifyUserProfileChange(
        userProfile: UserProfile,
        operation: String,
        currentUserId: String
    ) {
        val affectedUserIds = when (operation) {
            "created", "deleted" -> {
                supabaseRepository.getUserIdsByRole("admin")
                    .filter { it != currentUserId } // Don't notify self
            }
            "updated" -> {
                listOf(userProfile.user_id) +
                        supabaseRepository.getUserIdsByRole("admin")
                            .filter { it != currentUserId }
            }
            else -> emptyList()
        }.distinct()

        notifyCrudOperation(
            entityType = "user profile",
            operation = operation,
            entityId = userProfile.user_id,
            entityName = userProfile.name,
            currentUserId = currentUserId,
            affectedUserIds = affectedUserIds,
            additionalMetadata = mapOf(
                "role" to userProfile.role,
                "status" to userProfile.status
            )
        )
    }

    suspend fun notifyChatChange(
        chat: Chat,
        operation: String,
        currentUserId: String
    ) {
        val affectedUserIds = chat.participants
            .filter { it != currentUserId }
            .distinct()

        notifyCrudOperation(
            entityType = "chat",
            operation = operation,
            entityId = chat.chat_id,
            entityName = chat.breakdown_id?.let {
                "Chat for breakdown $it"
            } ?: "Group chat",
            currentUserId = currentUserId,
            affectedUserIds = affectedUserIds,
            additionalMetadata = mapOf(
                "breakdown_id" to (chat.breakdown_id ?: ""),
                "participants" to chat.participants.joinToString(",")
            )
        )
    }

    suspend fun notifyNewMessage(
        message: Message,
        currentUserId: String
    ) {
        try {
            // Get chat participants
            val chat = message.chat_id?.let {
                supabaseRepository.getChatByBreakdownId(it)
            } ?: return

            val affectedUserIds = chat.participants
                .filter { it != message.sender_id }
                .distinct()

            affectedUserIds.forEach { userId ->
                createNotification(
                    userId = userId,
                    title = "New message",
                    message = message.content.take(50).let {
                        if (it.length == 50) "$it..." else it
                    },
                    relatedId = message.chat_id ?: message.breakdown_id,
                    metadata = Json.encodeToString(mapOf(
                        "sender_id" to (message.sender_id ?: ""),
                        "breakdown_id" to (message.breakdown_id ?: "")
                    ))
                )
            }
        } catch (e: Exception) {
            Log.e("NotificationService", "Failed to send message notification", e)
        }
    }

    suspend fun notifyCompleteRequest(
        breakdownId: String,
        technicianId: String
    ) {
        try {
            Log.d("NotificationService", "Starting complete request notification")

            val breakdown = supabaseRepository.getBreakdownById(breakdownId)
            val technician = supabaseRepository.getUserProfile(technicianId)

            val equipmentName = breakdown?.equipment_id?.let { equipmentId ->
                supabaseRepository.getEquipmentById(equipmentId)?.identifier ?: "Unknown Equipment"
            } ?: "No Equipment"

            val mainMessage = "Technician ${technician?.name ?: "Unknown"} requests to mark " +
                    "breakdown ${equipmentName} as complete"

            val metadata = mapOf(
                "technician" to (technician?.name ?: "Unknown"),
                "breakdown" to "${equipmentName}: ${breakdown?.description?.take(50) ?: "No description"}",
                "type" to "complete_request"
            )

            val adminIds = supabaseRepository.getUserIdsByRole("admin")

            adminIds.forEach { adminId ->
                createNotification(
                    userId = adminId,
                    title = "Complete Request",
                    message = mainMessage,
                    relatedId = breakdownId,
                    metadata = Json.encodeToString(metadata)
                )
            }

            Log.d("NotificationService", "Notifications sent with formatted names")
        } catch (e: Exception) {
            Log.e("NotificationService", "Failed to send complete request", e)
            throw e
        }
    }
}