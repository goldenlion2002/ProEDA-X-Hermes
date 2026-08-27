package com.proedax.hermes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proedax.hermes.settings.AppSettings
import com.proedax.hermes.settings.SettingsManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsManager.observeSettings()

    fun saveSettings(apiKey: String, serverUrl: String, userName: String) {
        viewModelScope.launch {
            settingsManager.saveSettings(AppSettings(apiKey, serverUrl, userName))
        }
    }
}