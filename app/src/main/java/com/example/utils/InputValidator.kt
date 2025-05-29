package com.example.utils

import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class InputValidator {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun validateMinLength(input: EditText, minLength: Int, name: String): ValidationResult {
        return if (input.toTrimString().length >= minLength) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Min length for $name is $minLength.")
        }
    }


    fun validateEmail(input: EditText): ValidationResult {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(input.toTrimString()).matches()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Please provide your e-mail.")
        }
    }

    fun validateRequired(input: EditText, name: String): ValidationResult {
        return if (input.toTrimString().isNotBlank()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Please enter $name.")
        }
    }

    fun validateUrl(input: EditText): ValidationResult {
        val text = input.toTrimString()
        return if (android.util.Patterns.WEB_URL.matcher(text).matches()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Please enter a valid image URL.")
        }
    }

    suspend fun validateNicknameExists(
        editText: EditText,
        excludeUserId: String? = null
    ): ValidationResult {
        val nickname = editText.text.toString().trim()
        if (nickname.isEmpty()) return ValidationResult.Error("Nickname cannot be empty")

        val snapshot = firestore
            .collection("users")
            .whereEqualTo("nickname", nickname)
            .get()
            .await()

        val exists = snapshot.documents.any {
            val uid = it.getString("id")
            excludeUserId == null || uid != excludeUserId
        }

        return if (exists) {
            ValidationResult.Error("This nickname is already taken")
        } else {
            ValidationResult.Success
        }
    }

}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}