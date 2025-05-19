package com.example.libraryapp

import android.widget.*
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.books.BooksService
import com.example.models.Book
import com.example.user.UserService
import com.example.utils.InputValidator
import com.example.utils.ValidationResult
import com.example.utils.toTrimString
import kotlinx.coroutines.launch

class AddBookActivity : BaseActivity() {

    private lateinit var titleField: EditText
    private lateinit var authorField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var imageUrlField: EditText
    private lateinit var addBookButton: Button
    private lateinit var copiesField: EditText

    private lateinit var userService: UserService
    private lateinit var booksService: BooksService
    private val validator = InputValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        userService = UserService(this)
        booksService = BooksService(this, userService)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        titleField = findViewById(R.id.titleEditText)
        authorField = findViewById(R.id.authorEditText)
        descriptionField = findViewById(R.id.descriptionEditText)
        imageUrlField = findViewById(R.id.imageUrlEditText)
        addBookButton = findViewById(R.id.addBookButton)
        copiesField = findViewById(R.id.copiesEditText)
    }

    private fun setupListeners() {
        addBookButton.setOnClickListener {
            handleAddBook()
        }
    }

    private fun handleAddBook() {
        disableInputs(false)
        addBookButton.text = getString(R.string.btn_loading)

        val titleValidation = validator.validateRequired(titleField, "title")
        val authorValidation = validator.validateRequired(authorField, "author")
        val descriptionValidation = validator.validateRequired(descriptionField, "description")
        val imageUrlValidation = validator.validateUrl(imageUrlField)
        val copiesText = copiesField.toTrimString()
        val copies = copiesText.toIntOrNull()

        lifecycleScope.launch {
            when {
                titleValidation is ValidationResult.Error -> showToast(titleValidation.message)
                authorValidation is ValidationResult.Error -> showToast(authorValidation.message)
                descriptionValidation is ValidationResult.Error -> showToast(descriptionValidation.message)
                imageUrlValidation is ValidationResult.Error -> showToast(imageUrlValidation.message)
                copies == null || copies < 0 -> showToast("Please enter a valid number of copies")
                else -> performAddBook(copies)
            }

            disableInputs(true)
            addBookButton.text = getString(R.string.add_book)
        }
    }
    private fun clearFields() {
        titleField.text?.clear()
        authorField.text?.clear()
        descriptionField.text?.clear()
        imageUrlField.text?.clear()
        copiesField.text?.clear()
    }

    private suspend fun performAddBook(copies: Int) {
        val book = Book(
            title = titleField.toTrimString(),
            author = authorField.toTrimString(),
            description = descriptionField.toTrimString(),
            imagePath = imageUrlField.toTrimString(),
            copies = copies

        )

        val result = booksService.addBook(book)
        if (result != null) {
            showToast("Book added successfully.")
            clearFields()
        } else {
            showToast("Only admins can add books.")
        }
    }

    private fun disableInputs(enabled: Boolean) {
        titleField.isEnabled = enabled
        authorField.isEnabled = enabled
        descriptionField.isEnabled = enabled
        imageUrlField.isEnabled = enabled
        addBookButton.isEnabled = enabled
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
