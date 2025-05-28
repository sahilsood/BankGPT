package com.example.aichatapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RagRetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8003" // localhost

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: RagApiService = retrofit.create(RagApiService::class.java)
}