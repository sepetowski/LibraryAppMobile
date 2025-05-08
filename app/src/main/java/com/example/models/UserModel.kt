package com.example.models

import  com.example.enums.*

data class User(
    val id : String = "",
    val email: String = "",
    val nickname: String = "",
    val name: String? = null,
    val surname: String? = null,
    val role: UserRole = UserRole.USER
)
