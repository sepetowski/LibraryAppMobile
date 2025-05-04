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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val validator = InputValidator()
    private lateinit var userService: UserService

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerHref: TextView
    private lateinit var backLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userService = UserService(this)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        emailField = findViewById(R.id.emailEditText)
        passwordField = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerHref = findViewById(R.id.registerLink)
        backLayout = findViewById(R.id.backLayout)
    }

    private fun setupListeners() {
        backLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        registerHref.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        disableOrEnableInputs(false)
        loginButton.text = getString(R.string.btn_loading)

        val emailValidation = validator.validateEmail(emailField)
        val passwordValidation = validator.validateMinLength(passwordField, 6, "password")

        lifecycleScope.launch {
            when {
                emailValidation is ValidationResult.Error -> showToast(emailValidation.message)
                passwordValidation is ValidationResult.Error -> showToast(passwordValidation.message)
                else -> performLogin()
            }

            disableOrEnableInputs(true)
            loginButton.text = getString(R.string.login_btn)
        }
    }

    private suspend fun performLogin() {
        val loggedIn = userService.loginUser(
            emailField.toTrimString(),
            passwordField.toTrimString()
        )

        if (loggedIn) {
            val intent = Intent(this@LoginActivity, BooksActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun disableOrEnableInputs(enabled: Boolean) {
        registerHref.isClickable = enabled
        emailField.isEnabled = enabled
        passwordField.isEnabled = enabled
    }

    private fun showToast(message: String?) {
        Toast.makeText(this@LoginActivity, message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
