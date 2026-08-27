package com.proedax.hermes.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    companion object {
        private val AI_API_KEY = stringPreferencesKey("ai_api_key")
        private val AI_SERVER_URL = stringPreferencesKey("ai_server_url")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hermes_settings")
    }

    suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { preferences ->
            preferences[AI_API_KEY] = settings.aiApiKey
            preferences[AI_SERVER_URL] = settings.aiServerUrl
            preferences[USER_NAME] = settings.userName
        }
    }

    suspend fun getSettings(): AppSettings {
        val preferences = dataStore.data.map { prefs ->
            AppSettings(
                aiApiKey = prefs[AI_API_KEY] ?: "",
                aiServerUrl = prefs[AI_SERVER_URL] ?: "https://api.openai.com",
                userName = prefs[USER_NAME] ?: "User"
            )
        }
        var result = AppSettings()
        preferences.collect { result = it }
        return result
    }

    fun observeSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            aiApiKey = prefs[AI_API_KEY] ?: "",
            aiServerUrl = prefs[AI_SERVER_URL] ?: "https://api.openai.com",
            userName = prefs[USER_NAME] ?: "User"
        )
    }
}

data class AppSettings(
    val aiApiKey: String = "",
    val aiServerUrl: String = "https://api.openai.com",
    val userName: String = "User"
)