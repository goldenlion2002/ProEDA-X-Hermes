package com.proedax.hermes.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proedax.hermes.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()
    var apiKey by remember { mutableStateOf(settings.aiApiKey) }
    var serverUrl by remember { mutableStateOf(settings.aiServerUrl) }
    var userName by remember { mutableStateOf(settings.userName) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp).weight(1f)) {
            SettingItem("API Key", apiKey) { apiKey = it }
            SettingItem("Server URL", serverUrl) { serverUrl = it }
            SettingItem("User Name", userName) { userName = it }
        }

        Button(
            onClick = { viewModel.saveSettings(apiKey, serverUrl, userName) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Save Settings")
        }
    }
}

@Composable
fun SettingItem(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}