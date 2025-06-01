package com.example.project_we_fix_it.supabase

import com.example.project_we_fix_it.supabase.SupabaseClient
import com.example.project_we_fix_it.supabase.*
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
            client.from("user_profiles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): UserProfile = withContext(Dispatchers.IO) {
        try {
            client.from("user_profiles")
                .update({
                    set("name", profile.name)
                    set("role", profile.role)
                    set("phone", profile.phone)
                    set("location", profile.location)
                }) {
                    filter {
                        eq("user_id", profile.user_id)
                    }
                }
                .decodeSingle()
        } catch (e: Exception) {
            throw Exception("Error updating user profile: ${e.message}")
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