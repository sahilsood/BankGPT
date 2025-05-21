package com.example.aichatapp.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class ChatRequest(
    val model: String,
    val messages: List<Message> = listOf(
        Message(
            role = "user",
            content = "prompt"
        )
    )  // Assuming 'messages' is a list of message objects.
)

data class Message(
    val role: String,      // "user" or "assistant"
    val content: String    // The message text
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

interface ChatGPTApiService {
    @POST("v1/chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatRequest
    ): ChatResponse
}

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ChatGPTApiService by lazy {
        retrofit.create(ChatGPTApiService::class.java)
    }
}