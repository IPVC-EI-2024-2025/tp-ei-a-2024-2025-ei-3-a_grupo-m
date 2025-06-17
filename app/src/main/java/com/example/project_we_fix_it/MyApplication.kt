package com.example.project_we_fix_it

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.project_we_fix_it.supabase.SupabaseClient
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            SupabaseClient.initializeStorage()
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                ProcessLifecycleOwner.get().lifecycleScope.launch {
                    SupabaseClient.cleanup()
                }
            }
        })
    }
}