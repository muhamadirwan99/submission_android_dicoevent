package com.dicoding.dicoevent.di

import android.content.Context
import com.dicoding.dicoevent.data.EventRepository
import com.dicoding.dicoevent.data.local.room.NewsDatabase
import com.dicoding.dicoevent.data.pref.SettingPreferences
import com.dicoding.dicoevent.data.pref.dataStore
import com.dicoding.dicoevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = NewsDatabase.getInstance(context)
        val dao = database.eventDao()

        return EventRepository.getInstance(apiService, dao)
    }

    fun providePreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}