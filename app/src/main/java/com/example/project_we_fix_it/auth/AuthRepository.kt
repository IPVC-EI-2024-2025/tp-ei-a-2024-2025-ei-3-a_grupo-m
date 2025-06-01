package com.example.project_we_fix_it.auth

import com.example.project_we_fix_it.supabase.SupabaseClient
import com.example.project_we_fix_it.supabase.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val client = SupabaseClient.supabase

    // Check if user is currently logged in
    fun isUserLoggedIn(): Boolean {
        return client.auth.currentUserOrNull() != null
    }

    // Get current user info
    fun getCurrentUser(): UserInfo? {
        return client.auth.currentUserOrNull()
    }

    // Login with email and password
    suspend fun login(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = client.auth.currentUserOrNull()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }

    // Register new user
    suspend fun register(email: String, password: String, fullName: String): Result<UserInfo> = withContext(
        Dispatchers.IO) {
        try {
            println("DEBUG: Starting registration for email: $email, name: $fullName")

            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = client.auth.currentUserOrNull()
            if (user != null) {
                println("DEBUG: User created successfully with ID: ${user.id}")
                println("DEBUG: User email: ${user.email}")

                // Check if profile already exists
                val existingProfile = getUserProfileById(user.id)
                if (existingProfile != null) {
                    println("DEBUG: User profile already exists for user ${user.id}")

                    // Update the existing profile with the new name instead of returning early
                    updateUserProfile(user.id, fullName, email)
                    println("DEBUG: Updated existing user profile with new name: $fullName")
                } else {
                    // Create new user profile in database
                    createUserProfile(user.id, fullName, email)
                    println("DEBUG: Created new user profile with name: $fullName")
                }

                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: Exception) {
            println("DEBUG: Registration error: ${e.message}")
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }
    private suspend fun updateUserProfile(userId: String, fullName: String, email: String) {
        try {
            println("DEBUG: Updating user profile with:")
            println("DEBUG: - User ID: $userId")
            println("DEBUG: - Full Name: $fullName")
            println("DEBUG: - Email: $email")

            val userProfile = UserProfile(
                user_id = userId,
                name = fullName,
                role = "technician",
                status = "active",
                created_at = null
            )

            println("DEBUG: UserProfile object created for update: $userProfile")

            client.from("user_profiles")
                .update(userProfile) {
                    filter {
                        eq("user_id", userId)
                    }
                }

            println("DEBUG: User profile updated successfully")

        } catch (e: Exception) {
            println("DEBUG: Error updating user profile: ${e.message}")
            println("DEBUG: Full exception: $e")
        }
    }
    // Check if user profile exists
    private suspend fun getUserProfileById(userId: String): UserProfile? {
        return try {
            client.from("user_profiles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfile>()
        } catch (e: Exception) {
            println("DEBUG: Error checking existing profile: ${e.message}")
            null
        }
    }

    // Create user profile in database
    private suspend fun createUserProfile(userId: String, fullName: String, email: String) {
        try {
            println("DEBUG: Creating user profile with:")
            println("DEBUG: - User ID: $userId")
            println("DEBUG: - Full Name: $fullName")
            println("DEBUG: - Email: $email")

            val userProfile = UserProfile(
                user_id = userId,
                name = fullName,
                role = "technician",
                status = "active",
                created_at = null
            )

            println("DEBUG: UserProfile object created: $userProfile")

            client.from("user_profiles")
                .upsert(userProfile)

            println("DEBUG: User profile inserted successfully")

        } catch (e: Exception) {
            println("DEBUG: Error creating user profile: ${e.message}")
            println("DEBUG: Full exception: $e")
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Password reset failed: ${e.message}"))
        }
    }

    // Logout
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Logout failed: ${e.message}"))
        }
    }

    // Get user profile from database
    suspend fun getCurrentUserProfile(): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUser()?.id ?: return@withContext null
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

    fun getCurrentSession() = client.auth.currentSessionOrNull()

    suspend fun refreshSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Session refresh failed: ${e.message}"))
        }
    }

    suspend fun loadSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.auth.loadFromStorage()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Session load failed: ${e.message}"))
        }
    }

}