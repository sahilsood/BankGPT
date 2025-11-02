package com.example.aichatapp.data

import android.graphics.Bitmap
import java.util.UUID

data class Chat(
    val id: UUID = UUID.randomUUID(),
    val prompt: String,
    val bitmap:Bitmap? = null,
    val isFromUser: Boolean,
    val type: String = "text",
    val message: String = "",
    val selected: String = "",
    val recipient: String = "",
    val amount: String = "",
    val date: String = "",
    val thought: String = ""
)