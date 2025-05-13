package com.example.aichatapp.data

import android.graphics.Bitmap

sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class UpdateBitmap(val bitmap: Bitmap) : ChatUiEvent()
    data class SendPrompt(val prompt: String, val bitmap: Bitmap?) : ChatUiEvent()
}