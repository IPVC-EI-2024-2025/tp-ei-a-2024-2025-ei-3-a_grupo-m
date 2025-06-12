package com.example.project_we_fix_it.viewModels

import com.example.project_we_fix_it.supabase.SupabaseRepository
import com.example.project_we_fix_it.supabase.UserProfile

class ProfileManager(
    private val repository: SupabaseRepository
) {
    private val profileCache = mutableMapOf<String, UserProfile?>()

    suspend fun getProfile(userId: String): UserProfile? {
        return profileCache.getOrPut(userId) {
            repository.getUserProfile(userId)
        }
    }
}