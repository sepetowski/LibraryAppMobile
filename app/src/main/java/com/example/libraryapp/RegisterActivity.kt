package com.example.libraryapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.utils.InputValidator
import com.example.utils.ValidationResult
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.utils.toTrimString
import com.example.user.UserService
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private lateinit var auth: FirebaseAuth
    private val validator = InputValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        userService = UserService(this)

        val emailField = findViewById<EditText>(R.id.emailEditText)
        val nicknameField = findViewById<EditText>(R.id.nicknameText)
        val nameField = findViewById<EditText>(R.id.nameText)
        val surnameField = findViewById<EditText>(R.id.surnameText)

        val passwordField = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginHref = findViewById<TextView>(R.id.loginLink)
        val backLayout = findViewById<LinearLayout>(R.id.backLayout)

        val inputFields = listOf(
            emailField, nicknameField, nameField, surnameField, passwordField
        )

        loginHref.setOnClickListener {
            // startActivity(Intent(this, LoginActivity::class.java))
        }

        backLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        fun disableOrEnableInputs(enabled: Boolean = true) {
            loginHref.isClickable = enabled
            inputFields.forEach { it.isEnabled = enabled }
        }

        fun clearInputs() {
            inputFields.forEach { it.text.clear() }
        }

        registerButton.setOnClickListener {
            disableOrEnableInputs(false)
            registerButton.text = getString(R.string.btn_loading)

            val emailValidation = validator.validateEmail(emailField)
            val passwordValidation = validator.validateMinLength(passwordField, 6, "password")
            val nicknameValidation = validator.validateMinLength(nicknameField, 4, "nickname")

            lifecycleScope.launch {
                val nicknameExistsValidation = validator.validateNicknameExists(nicknameField)

                when {
                    emailValidation is ValidationResult.Error -> {
                        Toast.makeText(this@RegisterActivity, emailValidation.message, Toast.LENGTH_SHORT).show()
                    }

                    passwordValidation is ValidationResult.Error -> {
                        Toast.makeText(this@RegisterActivity, passwordValidation.message, Toast.LENGTH_SHORT).show()
                    }

                    nicknameValidation is ValidationResult.Error -> {
                        Toast.makeText(this@RegisterActivity, nicknameValidation.message, Toast.LENGTH_SHORT).show()
                    }

                    nicknameExistsValidation is ValidationResult.Error -> {
                        Toast.makeText(this@RegisterActivity, nicknameExistsValidation.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        userService.registerUser(
                            emailField.toTrimString(),
                            passwordField.toTrimString(),
                            nicknameField.toTrimString(),
                            nameField.toTrimString(),
                            surnameField.toTrimString()
                        )
                        clearInputs()
                    }
                }

                disableOrEnableInputs(true)
                registerButton.text = getString(R.string.register_btn)
            }
        }
    }
}