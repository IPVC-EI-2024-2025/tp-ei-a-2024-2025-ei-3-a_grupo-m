package com.example.project_we_fix_it.supabase

import com.example.project_we_fix_it.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object SupabaseClient {
    const val BUCKET_NAME = "breakdown-photos"

    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Storage)
        install(Auth) {
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
    }

    // Initialize the storage bucket (call this once at app startup)
    suspend fun initializeStorage() = withContext(Dispatchers.IO) {
        try {
            supabase.storage.createBucket(BUCKET_NAME){
                public = true
                fileSizeLimit = 5.megabytes
            }
            Log.d("SupabaseClient", "Storage bucket initialized")
        } catch (e: Exception) {
            // Bucket likely already exists
            Log.d("SupabaseClient", "Bucket initialization: ${e.message}")
        }
    }
}