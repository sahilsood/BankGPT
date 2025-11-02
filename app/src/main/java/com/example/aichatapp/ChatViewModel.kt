package com.example.aichatapp

import android.graphics.Bitmap
import android.util.Log
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

const val TRANSFER_CONFIRM = "transfer"

const val ZELLE_CONFIRM = "zelle"

const val ACTION = "action"

const val ZELLE_ACTION = "zelle_action"

private const val AMOUNT = "amount"

const val TRANSFER_AMOUNT = AMOUNT

private const val DATE = "date"

const val TRANSFER_DATE = DATE

private const val RECIPIENT = "recipient"

const val TRANSFER_RECIPIENT = RECIPIENT

const val ZELLE_RECIPIENT = "zelle_recipient"

private const val USER = "user"

private const val SYSTEM = "system"

private const val NEXT = "next"

private const val THOUGHT = "thought"

private const val MESSAGE = "message"

private const val SELECTED = "selected"

class ChatViewModel : ViewModel() {
    private val repository: ChatRepository = ChatRepository()
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    // Initialize conversation history with the system message
    private val _conversationHistory = MutableStateFlow(
        listOf(
            Message(
                role = SYSTEM,
                content = getBankGptPrompt().trimIndent()
            )
        )
    )
    val conversationHistory = _conversationHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

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
        addToHistory(Message(role = USER, content = prompt))

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

    private fun getChatGPTResponse(useRag: Boolean = false) {
        viewModelScope.launch {
            setLoading(value = true)
            Log.d("ChatViewModel", "getChatGPTResponse invoked with useRag = $useRag")

            try {
                val responseContent = if (useRag) {
                    val latestUserPrompt =
                        _conversationHistory.value.lastOrNull { it.role == "user" }?.content
                    Log.d("ChatViewModel", "Latest user prompt for RAG: $latestUserPrompt")

                    latestUserPrompt?.let { query ->
                        val ragResponse = repository.getRagResponse(prompt = query)
                        Log.d("ChatViewModel", "RAG response: $ragResponse")
                        ragResponse
                    }
                } else {
                    val response = repository.getChatResponse(_conversationHistory.value)
                    Log.d("ChatViewModel", "ChatGPT API response: $response")

                    response?.choices?.firstOrNull()?.message?.content
                }

                responseContent?.let { content ->
                    Log.d("ChatViewModel", "Received assistant content: $content")
                    setLoading(value = false)
                    val message = Message(role = "assistant", content = content)
                    addToHistory(message)
                    handleAssistantResponse(content)
                } ?: run {
                    Log.e("ChatViewModel", "Response content is null")
                    setLoading(value = false)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception in getChatGPTResponse: ${e.message}", e)
                setLoading(value = false)
            }
        }
    }

    private fun handleAssistantResponse(content: String) {
        try {
            val jsonResponse = JSONObject(content)
            val next = jsonResponse.optString(NEXT, "text")
            val thought = jsonResponse.optString(THOUGHT, "")
            val message = jsonResponse.optString(MESSAGE, "")
            val selected = jsonResponse.optString(SELECTED, "")
            val recipient = jsonResponse.optString(RECIPIENT, "")
            val amount = jsonResponse.optString(AMOUNT, "")
            val date = jsonResponse.optString(DATE, "")

            val chat = Chat(
                prompt = content,
                bitmap = null,
                isFromUser = false,
                type = next,
                message = message,
                selected = selected,
                recipient = recipient,
                amount = amount,
                date = date,
                thought = thought
            )

            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }

        } catch (e: Exception) {
            // Handle non-JSON or malformed content as a regular message
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