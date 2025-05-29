package com.example.libraryapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.user.UserService
import com.example.utils.InputValidator
import com.example.utils.ValidationResult
import com.example.utils.toTrimString
import kotlinx.coroutines.launch

class UserProfileActivity : BaseActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var userService: UserService
    private val validator = InputValidator()

    private var originalNickname: String = ""
    private var originalName: String? = ""
    private var originalSurname: String? = ""

    private var hasInitializedFields = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userService = UserService(this)

        nicknameEditText = findViewById(R.id.nicknameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        saveButton = findViewById(R.id.saveButton)

        saveButton.isEnabled = false
        setupListeners()
    }

    override fun onUserReady() {
        if (!hasInitializedFields) {
            populateFields()
            hasInitializedFields = true
        }
    }

    private fun populateFields() {
        user?.let {
            originalNickname = it.nickname
            originalName = it.name ?: ""
            originalSurname = it.surname ?: ""

            nicknameEditText.setText(originalNickname)
            nameEditText.setText(originalName)
            surnameEditText.setText(originalSurname)
        }
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = checkForChanges()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        nicknameEditText.addTextChangedListener(watcher)
        nameEditText.addTextChangedListener(watcher)
        surnameEditText.addTextChangedListener(watcher)

        saveButton.setOnClickListener {
            handleSave()
        }
    }

    private fun checkForChanges() {
        val currentNickname = nicknameEditText.toTrimString()
        val currentName = nameEditText.toTrimString()
        val currentSurname = surnameEditText.toTrimString()

        saveButton.isEnabled = currentNickname != originalNickname ||
                currentName != originalName ||
                currentSurname != originalSurname
    }

    private fun handleSave() {
        disableOrEnableInputs(false)
        saveButton.text = getString(R.string.btn_loading)

        val newNickname = nicknameEditText.toTrimString()
        val newName = nameEditText.toTrimString()
        val newSurname = surnameEditText.toTrimString()

        val nicknameValidation = validator.validateMinLength(nicknameEditText, 4, "nickname")

        lifecycleScope.launch {
            val nicknameExistsValidation = validator.validateNicknameExists(
                nicknameEditText,
                excludeUserId = user?.id
            )

            when {
                nicknameValidation is ValidationResult.Error -> showToast(nicknameValidation.message)
                nicknameExistsValidation is ValidationResult.Error -> showToast(nicknameExistsValidation.message)
                else -> performProfileUpdate(newNickname, newName, newSurname)
            }

            disableOrEnableInputs(true)
            saveButton.text = getString(R.string.save)
        }
    }

    private suspend fun performProfileUpdate(nickname: String, name: String?, surname: String?) {
        val updatedUser = user?.copy(
            nickname = nickname,
            name = name,
            surname = surname
        )

        updatedUser?.let {
            try {
                userService.updateUserProfile(it)

                originalNickname = nickname
                originalName = name ?: ""
                originalSurname = surname ?: ""

                checkForChanges()

                Toast.makeText(this@UserProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@UserProfileActivity, "Failed to update profile", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun disableOrEnableInputs(enabled: Boolean) {
        nicknameEditText.isEnabled = enabled
        nameEditText.isEnabled = enabled
        surnameEditText.isEnabled = enabled
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
