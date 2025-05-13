package com.example.aichatapp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.Chat
import com.example.aichatapp.data.ChatState
import com.example.aichatapp.data.ChatUiEvent
import com.example.aichatapp.data.getBankGptPrompt
import com.example.aichatapp.repository.ChatRepository
import com.example.aichatapp.service.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel : ViewModel() {
    private val repository: ChatRepository = ChatRepository()
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    // Initialize conversation history with the system message
    private val _conversationHistory = MutableStateFlow(
        listOf(
            Message(
                role = "system",
                content = getBankGptPrompt().trimIndent()
            )
        )
    )
    val conversationHistory = _conversationHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    // Adds a message to history
    private fun addToHistory(message: Message) {
        _conversationHistory.value += message
    }

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }

            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    setLoading(value = true)
                    addPrompt(event.prompt, event.bitmap)
                    getChatGPTResponse()
                }
            }

            else -> { /* Handle other events */
            }
        }
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        // Add user's prompt to conversation history
        addToHistory(Message(role = "user", content = prompt))

        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt = prompt, bitmap = bitmap, isFromUser = true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }

    private fun getChatGPTResponse() {
        viewModelScope.launch {
            val response = repository.getChatResponse(_conversationHistory.value)

            // Process assistant's response and add it to conversation history
            response?.choices?.firstOrNull()?.message?.let { assistantMessage ->
                setLoading(value = false)
                addToHistory(assistantMessage)
                handleAssistantResponse(assistantMessage.content)
            }
        }
    }

    private fun handleAssistantResponse(content: String) {
        try {
            val jsonResponse = JSONObject(content)
            when (jsonResponse.getString("next")) {
                "date" -> {
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(
                                    0,
                                    Chat(
                                        prompt = content,
                                        bitmap = null,
                                        isFromUser = false,
                                        type = "date"
                                    )
                                )
                            }
                        )
                    }
                }

                "account" -> {
                    // Trigger account picker UI
                }

                "action" -> {
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(
                                    0,
                                    Chat(
                                        prompt = content,
                                        bitmap = null,
                                        isFromUser = false,
                                        type = "action"
                                    )
                                )
                            }
                        )
                    }
                }

                "recipient" -> {
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(
                                    0,
                                    Chat(
                                        prompt = content,
                                        bitmap = null,
                                        isFromUser = false,
                                        type = "recipient"
                                    )
                                )
                            }
                        )
                    }
                }

                "amount" -> {
                    // Trigger amount text input field
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(
                                    0,
                                    Chat(
                                        prompt = content,
                                        bitmap = null,
                                        isFromUser = false,
                                        type = "amount"
                                    )
                                )
                            }
                        )
                    }
                }

                "transfer" -> {
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(
                                    0,
                                    Chat(
                                        prompt = content,
                                        bitmap = null,
                                        isFromUser = false,
                                        type = "transfer_money"
                                    )
                                )
                            }
                        )
                    }
                }

                else -> {
                    // Handle other cases if needed
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(0, Chat(prompt = content, bitmap = null, isFromUser = false))
                            }
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Handle other cases if needed
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, Chat(prompt = content, bitmap = null, isFromUser = false))
                    }
                )
            }
        }
    }
}