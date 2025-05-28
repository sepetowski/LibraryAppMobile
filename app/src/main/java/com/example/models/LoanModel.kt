package com.example.models

data class Loan(
    var id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var returnedAt: Long? = null

)
