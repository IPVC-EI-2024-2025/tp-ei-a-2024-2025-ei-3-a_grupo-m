package com.example.project_we_fix_it.supabase

import android.content.ContentValues.TAG
import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

@Singleton
class SupabaseRepository @Inject constructor() {

    private val client = SupabaseClient.supabase

    // ========== USER PROFILES ==========
    suspend fun getUserProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching profile for user: $userId")
            client.from("user_profiles")
                .select {
                    filter { eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profile for $userId: ${e.message}")
            null
        }
    }

    suspend fun getAllUsers(): List<UserProfile> = withContext(Dispatchers.IO) {
        client.from("user_profiles").select().decodeList()
    }

    suspend fun deleteUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("user_profiles").delete {
                filter { eq("user_id", userId) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserEmail(userId: String, newEmail: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get the current user session first
            val session = client.auth.currentSessionOrNull()
                ?: throw Exception("No active session")

            // Update the email - this will trigger a confirmation email
            val result = client.auth.updateUser {
                email = newEmail
            }

            Log.d(TAG, "Email update initiated for $userId. Confirmation sent to $newEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Email update failed for $userId: ${e.message}")
            false
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): UserProfile = withContext(Dispatchers.IO) {
        try {
            // Update email first if changed
            profile.email?.let { newEmail ->
                val currentUser = client.auth.currentUserOrNull()
                if (currentUser?.email != newEmail) {
                    val emailUpdated = updateUserEmail(profile.user_id, newEmail)
                    if (!emailUpdated) {
                        throw Exception("Email update failed")
                    }
                }
            }

            // Then update profile data
            client.from("user_profiles")
                .update({
                    set("name", profile.name)
                    set("role", profile.role)
                    set("phone", profile.phone)
                    set("location", profile.location)
                    set("status", profile.status)
                }) {
                    filter { eq("user_id", profile.user_id) }
                }

            // Return the updated profile
            client.from("user_profiles")
                .select { filter { eq("user_id", profile.user_id) } }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Profile update failed: ${e.message}")
        }
    }
    // ========== EQUIPMENT ==========
    suspend fun getAllEquipment(): List<Equipment> = withContext(Dispatchers.IO) {
        try {
            client.from("equipment")
                .select()
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching equipment: ${e.message}")
        }
    }

    suspend fun createEquipment(equipment: Equipment): Equipment = withContext(Dispatchers.IO) {
        try {
            require(equipment.equipment_id == null) { "Equipment ID must be null for creation" }

            client.from("equipment")
                .insert(equipment.copy(equipment_id = null))
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error creating equipment: ${e.message}")
        }
    }

    suspend fun updateEquipment(equipment: Equipment): Equipment = withContext(Dispatchers.IO) {
        try {
            require(equipment.equipment_id != null) { "Equipment ID cannot be null for update" }

            client.from("equipment")
                .update({
                    set("identifier", equipment.identifier)
                    set("type", equipment.type)
                    set("model", equipment.model)
                    set("location", equipment.location)
                    set("status", equipment.status)
                }) {
                    filter { eq("equipment_id", equipment.equipment_id) }
                }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating equipment: ${e.message}")
        }
    }

    suspend fun deleteEquipment(equipmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("equipment").delete {
                filter { eq("equipment_id", equipmentId) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getEquipmentById(equipmentId: String): Equipment? = withContext(Dispatchers.IO) {
        try {
            client.from("equipment")
                .select {
                    filter {
                        eq("equipment_id", equipmentId)
                    }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            throw Exception("Error fetching equipment: ${e.message}")
        }
    }

    // ========== BREAKDOWNS ==========
    suspend fun getBreakdownById(breakdownId: String): Breakdown? = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .select {
                    filter {
                        eq("breakdown_id", breakdownId)
                    }
                }
                .decodeSingle<Breakdown>()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching breakdown: ${e.message}")
            null
        }
    }
    suspend fun updateBreakdown(breakdown: Breakdown): Breakdown = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .update({
                    set("reporter_id", breakdown.reporter_id)
                    set("equipment_id", breakdown.equipment_id)
                    set("urgency_level", breakdown.urgency_level)
                    set("location", breakdown.location)
                    set("description", breakdown.description)
                    set("status", breakdown.status)
                    set("reported_at", breakdown.reported_at)
                    set("estimated_completion", breakdown.estimated_completion)
                }) {
                    filter { breakdown.breakdown_id?.let { eq("breakdown_id", it) } }
                    }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating breakdown: ${e.message}")
        }
    }

    suspend fun deleteBreakdown(breakdownId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns").delete {
                filter { eq("breakdown_id", breakdownId) }
            }
            true
            } catch (e: Exception) {
                false
        }
    }


    suspend fun getAllBreakdowns(): List<Breakdown> = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .select()
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching breakdowns: ${e.message}")
        }
    }

    suspend fun getBreakdownsByStatus(status: String): List<Breakdown> = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .select {
                    filter {
                        eq("status", status)
                    }
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching breakdowns by status: ${e.message}")
        }
    }

    suspend fun getBreakdownsByUrgency(urgency: String): List<Breakdown> = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .select {
                    filter {
                        eq("urgency_level", urgency)
                    }
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching breakdowns by urgency: ${e.message}")
        }
    }

    suspend fun createBreakdown(breakdown: Breakdown): Breakdown = withContext(Dispatchers.IO) {
        try {
            require(breakdown.breakdown_id == null) { "Breakdown ID must be null for creation" }

            client.from("breakdowns")
                .insert(breakdown.copy(breakdown_id = null)) // Explicitly exclude ID
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error creating breakdown: ${e.message}")
        }
    }

    suspend fun updateBreakdownStatus(breakdownId: String, status: String): Breakdown = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .update({
                    set("status", status)
                }) {
                    filter {
                        eq("breakdown_id", breakdownId)
                    }
                }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating breakdown status: ${e.message}")
        }
    }

    suspend fun updateBreakdownUrgency(breakdownId: String, urgency: String): Breakdown = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .update({
                    set("urgency_level", urgency)
                }) {
                    filter {
                        eq("breakdown_id", breakdownId)
                    }
                }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating breakdown urgency level: ${e.message}")
        }
    }

    suspend fun getBreakdownsByReporter(reporterId: String): List<Breakdown> = withContext(Dispatchers.IO) {
        try {
            client.from("breakdowns")
                .select {
                    filter {
                        eq("reporter_id", reporterId)
                    }
                    order("reported_at", Order.DESCENDING)
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching breakdowns by reporter: ${e.message}")
        }
    }

    // ========== ASSIGNMENTS ==========
    suspend fun getAllAssignments(): List<Assignment> = withContext(Dispatchers.IO) {
        try {
            client.from("assignments")
                .select()
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching assignments: ${e.message}")
        }
    }

    suspend fun getAssignmentsByTechnician(technicianId: String): List<Assignment> = withContext(Dispatchers.IO) {
        try {
            client.from("assignments")
                .select {
                    filter {
                        eq("technician_id", technicianId)
                        eq("status", "active")
                    }
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching assignments: ${e.message}")
        }
    }

    suspend fun createAssignment(assignment: Assignment): Assignment = withContext(Dispatchers.IO) {
        try {
            client.from("assignments")
                .insert(assignment)
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error creating assignment: ${e.message}")
        }
    }

    suspend fun updateAssignmentStatus(assignmentId: String, status: String): Assignment = withContext(Dispatchers.IO) {
        try {
            client.from("assignments")
                .update({
                    set("status", status)
                }) {
                    filter {
                        eq("assignment_id", assignmentId)
                    }
                }
                .decodeSingle()
                } catch (e: Exception) {
                    throw Exception("Error updating assignment status: ${e.message}")

                }
    }
    suspend fun deleteAssignment(assignmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("assignments").delete {
                filter { eq("assignment_id", assignmentId) }
            }
            true
            } catch (e: Exception) {
                false
            }
    }
    // ========== BREAKDOWN PHOTOS ==========
    suspend fun getBreakdownPhotos(breakdownId: String): List<BreakdownPhoto> = withContext(Dispatchers.IO) {
        try {
            client.from("breakdown_photos")
                .select {
                    filter {
                        eq("breakdown_id", breakdownId)
                    }
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching breakdown photos: ${e.message}")
        }
    }

    suspend fun addBreakdownPhoto(photo: BreakdownPhoto): BreakdownPhoto = withContext(Dispatchers.IO) {
        try {
            client.from("breakdown_photos")
                .insert(photo)
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error adding breakdown photo: ${e.message}")
        }
    }
    suspend fun deleteBreakdownPhoto(photoId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("breakdown_photos").delete {
                filter { eq("photo_id", photoId) }
            }
            true
            } catch (e: Exception) {
                false
            }
    }

    suspend fun updateBreakdownPhoto(photo: BreakdownPhoto): BreakdownPhoto = withContext(Dispatchers.IO) {
        try {
            client.from("breakdown_photos")
                .update({
                    set("photo_url", photo.photo_url)
                }) {
                    filter { photo.photo_id?.let { eq("photo_id", it) } }
                    }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating breakdown photo: ${e.message}")
        }
    }
    // ========== MESSAGES ==========
    suspend fun getMessagesByBreakdown(breakdownId: String): List<Message> = withContext(Dispatchers.IO) {
        try {
            client.from("messages")
                .select {
                    filter {
                        eq("breakdown_id", breakdownId)
                    }
                    order("sent_at", Order.ASCENDING)
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching messages: ${e.message}")
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun sendMessage(message: Message): Message = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Sending message: $message")

            // Validate required fields
            message.sender_id?.let { require(it.isNotBlank()) { "Sender ID is required" } }
            require(message.content.isNotBlank()) { "Message content is required" }

            // Insert the message and get the result
            val insertedMessage = client.from("messages")
                .insert(message.copy(message_id = null)) // Ensure ID is null for insertion
                .decodeSingle<Message>()

            Log.d(TAG, "Message inserted successfully: ${insertedMessage.message_id}")

            // Update chat's last message timestamp if chat_id exists
            message.chat_id?.let { chatId ->
                try {
                    client.from("chats")
                        .update({
                            set("last_message_at", now())
                        }) {
                            filter { eq("chat_id", chatId) }
                        }
                    Log.d(TAG, "Chat timestamp updated for chat: $chatId")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update chat timestamp: ${e.message}")
                    // Don't throw here, the message was sent successfully
                }
            }

            insertedMessage
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}", e)
            throw Exception("Error sending message: ${e.message}")
        }
    }


    // ========== TECHNICIAN METRICS ==========
    suspend fun getTechnicianMetrics(technicianId: String): TechnicianMetrics? = withContext(Dispatchers.IO) {
        try {
            client.from("technician_metrics")
                .select {
                    filter {
                        eq("technician_id", technicianId)
                    }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            throw Exception("Error fetching technician metrics: ${e.message}")
        }
    }

    // ========== CHATS ==========
    suspend fun createChat(breakdownId: String?, participants: List<String>): Chat = withContext(Dispatchers.IO) {
        try {
            client.from("chats")
                .insert(Chat(
                    breakdown_id = breakdownId,
                    participants = participants
                ))
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error creating chat: ${e.message}")
        }
    }


    suspend fun getChatByBreakdownId(breakdownId: String): Chat? = withContext(Dispatchers.IO) {
        try {
            client.from("chats")
                .select {
                    filter {
                        eq("breakdown_id", breakdownId)
                        }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            throw Exception("Error fetching chat: ${e.message}")
        }
    }

    suspend fun getChatsForUser(userId: String): List<Chat> = withContext(Dispatchers.IO) {
        try {
            client.from("chats")
                .select {
                    filter {
                        contains("participants", listOf(userId))
                    }
                    order("last_message_at", Order.DESCENDING)
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching chats: ${e.message}")
        }
    }

   suspend fun getAllChats(): List<Chat> = withContext(Dispatchers.IO) {
        try {
            client.from("chats")
                .select()
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching chats: ${e.message}")
        }
    }

   suspend fun getMessagesByChat(chatId: String): List<Message> = withContext(Dispatchers.IO) {
        try {
            client.from("messages")
            .select {
                filter {
                    eq("chat_id", chatId)
                }
                order("sent_at", Order.ASCENDING)
                }
                .decodeList()
        } catch (e: Exception) {
            throw Exception("Error fetching messages: ${e.message}")
        }
    }

}


















