package com.example.enums

enum class UserRole(val roleName: String) {
    USER("User"),
    ADMIN("Admin");

    companion object {
        fun fromString(roleName: String): UserRole {
            return entries.firstOrNull { it.roleName.equals(roleName, ignoreCase = true) }
                ?: USER
        }
    }
}