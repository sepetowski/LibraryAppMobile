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

    fun validateNotEmpty(input: EditText): ValidationResult {
        return if (input.toTrimString().isNotBlank()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("This filed cannot be empty.")
        }
    }

    fun validateEmail(input: EditText): ValidationResult {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(input.toTrimString()).matches()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Please provide your e-mail.")
        }
    }

    suspend fun validateNicknameExists(nickname: EditText): ValidationResult {
        val firestore = FirebaseFirestore.getInstance()

        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("nickname", nickname.toTrimString())
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                ValidationResult.Error("This nickname is already taken.")
            } else {
                ValidationResult.Success
            }
        } catch (e: Exception) {
            ValidationResult.Error("There was an error while validating nickname. Try again.")
        }
    }

}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}