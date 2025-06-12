package com.example.project_we_fix_it

import com.example.project_we_fix_it.auth.AuthRepository
import com.example.project_we_fix_it.supabase.SupabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = AuthRepository()

    @Provides
    @Singleton
    fun provideSupabaseRepository(): SupabaseRepository = SupabaseRepository()
}