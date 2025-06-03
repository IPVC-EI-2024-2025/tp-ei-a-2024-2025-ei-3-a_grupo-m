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
        client.from("equipment").insert(equipment).decodeSingle()
    }

    suspend fun updateEquipment(equipment: Equipment): Equipment = withContext(Dispatchers.IO) {
        try {
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
                    filter {eq("breakdown_id", breakdown.breakdown_id)  }
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
            client.from("breakdowns")
                .insert(breakdown)
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

    // ========== ASSIGNMENTS ==========
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

    suspend fun sendMessage(message: Message): Message = withContext(Dispatchers.IO) {
        try {
            client.from("messages")
                .insert(message)
                .decodeSingle()
        } catch (e: Exception) {
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

}