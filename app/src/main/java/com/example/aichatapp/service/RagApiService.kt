package com.example.aichatapp.service

import com.example.aichatapp.data.RagResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RagApiService {
    @POST("/rag-query")
    suspend fun queryRag(@Body request: Map<String, String>): RagResponse
}