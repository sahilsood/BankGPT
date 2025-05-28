package com.example.aichatapp.repository

import com.example.aichatapp.BuildConfig
import com.example.aichatapp.service.ChatRequest
import com.example.aichatapp.service.ChatResponse
import com.example.aichatapp.service.Message
import com.example.aichatapp.service.RagRetrofitInstance
import com.example.aichatapp.service.RetrofitInstance

class ChatRepository {

    suspend fun getChatResponse(chat: List<Message>): ChatResponse? {
        val request = ChatRequest(
            model = "gpt-4o-mini",
            messages = chat
        )
        return try {
            RetrofitInstance.apiService.getChatResponse(
                request = request,
                authHeader = "Bearer ${BuildConfig.GPT_AUTH_KEY}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getRagResponse(prompt: String): String? {
        return try {
            val response = RagRetrofitInstance.api.queryRag(mapOf("query" to prompt))
            response.answer
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}