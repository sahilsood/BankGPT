package com.example.aichatapp.data

import android.graphics.Bitmap

data class Chat(
    val prompt: String,
    val bitmap:Bitmap? = null,
    val isFromUser: Boolean,
    val type: String = "text",
    val message: String = "",
    val selected: String = "",
    val recipient: String = "",
    val amount: String = "",
    val date: String = ""
)