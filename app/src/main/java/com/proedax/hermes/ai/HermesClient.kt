package com.proedax.hermes.ai

import com.proedax.hermes.settings.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class HermesClient(private val settingsManager: SettingsManager) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(
        messages: List<Map<String, String>>,
        tools: List<Map<String, Any>>? = null
    ): AiResponse = withContext(Dispatchers.IO) {
        try {
            val settings = settingsManager.getSettings()
            val url = "${settings.aiServerUrl}/v1/chat/completions"
            
            val requestBody = mapOf(
                "model" to "gpt-4",
                "messages" to messages,
                "tools" to (tools ?: emptyList()),
                "temperature" to 0.7
            )
            
            val json = com.squareup.moshi.Moshi.Builder().build()
                .adapter(Map::class.java).toJson(requestBody)
            
            val request = Request.Builder()
                .url(url)
                .post(json!!.toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer ${settings.aiApiKey}")
                .build()
            
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                AiResponse.Success("Response received", null)
            } else {
                AiResponse.Error("API Error: ${response.code}")
            }
        } catch (e: Exception) {
            AiResponse.Error(e.message ?: "Unknown error")
        }
    }

    fun sendMessageStreaming(
        messages: List<Map<String, String>>,
        tools: List<Map<String, Any>>? = null
    ): Flow<String> = flow {
        emit("Streaming response initialized...")
    }.flowOn(Dispatchers.IO)
}

sealed class AiResponse {
    data class Success(val content: String?, val toolCalls: List<ToolCall>?) : AiResponse()
    data class Error(val message: String) : AiResponse()
}

data class ToolCall(val id: String, val name: String, val arguments: Map<String, Any>)