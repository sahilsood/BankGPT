package com.example.aichatapp.data

data class TransferState(
    val intent: String? = null,
    val fromAccount: String? = null,
    val toAccount: String? = null,
    val amount: Double? = null,
    val date: String? = null
) {
    val isComplete: Boolean
        get() = fromAccount != null && toAccount != null && amount != null && date != null
}