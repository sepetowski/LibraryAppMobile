package com.example.models

data class LoanDisplay(
    val loanId: String,
    val userId: String,
    val userNickname: String,
    val bookId: String,
    val bookTitle: String,
    val imagePath: String,
    val timestamp: Long,
    val returnedAt: Long? = null
)
