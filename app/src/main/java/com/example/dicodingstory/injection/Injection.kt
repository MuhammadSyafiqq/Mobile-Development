package com.example.dicodingstory.injection

import android.content.Context
import android.util.Log
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.utils.ApiConfig
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val token = user.token

        Log.d("Injection", "Retrieved token: $token")

        val apiService = ApiConfig.getService(token)
        return StoryRepository(apiService)
    }
}
