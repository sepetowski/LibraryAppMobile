package com.example.libraryapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.user.UserService
import com.example.utils.InputValidator
import com.example.utils.ValidationResult
import com.example.utils.toTrimString
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var userService: UserService
    private lateinit var auth: FirebaseAuth
    private val validator = InputValidator()

    private lateinit var emailField: EditText
    private lateinit var nicknameField: EditText
    private lateinit var nameField: EditText
    private lateinit var surnameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var registerButton: Button
    private lateinit var loginHref: TextView
    private lateinit var backLayout: LinearLayout

    private lateinit var inputFields: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        userService = UserService(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        emailField = findViewById(R.id.emailEditText)
        nicknameField = findViewById(R.id.nicknameText)
        nameField = findViewById(R.id.nameText)
        surnameField = findViewById(R.id.surnameText)
        passwordField = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginHref = findViewById(R.id.loginLink)
        backLayout = findViewById(R.id.backLayout)

        inputFields = listOf(emailField, nicknameField, nameField, surnameField, passwordField)
    }

    private fun setupListeners() {
        loginHref.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        backLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        registerButton.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        disableOrEnableInputs(false)
        registerButton.text = getString(R.string.btn_loading)

        val emailValidation = validator.validateEmail(emailField)
        val passwordValidation = validator.validateMinLength(passwordField, 6, "password")
        val nicknameValidation = validator.validateMinLength(nicknameField, 4, "nickname")

        lifecycleScope.launch {
            val nicknameExistsValidation = validator.validateNicknameExists(nicknameField)

            when {
                emailValidation is ValidationResult.Error -> showToast(emailValidation.message)
                passwordValidation is ValidationResult.Error -> showToast(passwordValidation.message)
                nicknameValidation is ValidationResult.Error -> showToast(nicknameValidation.message)
                nicknameExistsValidation is ValidationResult.Error -> showToast(nicknameExistsValidation.message)
                else -> performRegistration()
            }

            disableOrEnableInputs(true)
            registerButton.text = getString(R.string.register_btn)
        }
    }

    private suspend fun performRegistration() {
        val success = userService.registerUser(
            email = emailField.toTrimString(),
            password = passwordField.toTrimString(),
            nickname = nicknameField.toTrimString(),
            name = nameField.toTrimString(),
            surname = surnameField.toTrimString()
        )

        if (success) {
            val intent = Intent(this@RegisterActivity, BooksActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun disableOrEnableInputs(enabled: Boolean) {
        loginHref.isClickable = enabled
        inputFields.forEach { it.isEnabled = enabled }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
