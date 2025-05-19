package com.example.libraryapp

import android.os.Bundle
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.books.BooksService
import com.example.models.Book
import com.example.user.UserService
import com.example.utils.InputValidator
import com.example.utils.ValidationResult
import com.example.utils.toTrimString
import kotlinx.coroutines.launch

class EditBookActivity : BaseActivity() {

    private lateinit var titleField: EditText
    private lateinit var authorField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var imageUrlField: EditText
    private lateinit var copiesField: EditText
    private lateinit var updateButton: Button

    private lateinit var booksService: BooksService
    private lateinit var userService: UserService
    private val validator = InputValidator()
    private var bookId: String? = null
    private var loadedBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        userService = UserService(this)
        booksService = BooksService(this, userService)

        initViews()

        bookId = intent.getStringExtra("BOOK_ID")
        bookId?.let { loadBookData(it) }

        updateButton.text = "Update Book"
        updateButton.setOnClickListener { handleUpdateBook() }
    }

    private fun initViews() {
        titleField = findViewById(R.id.titleEditText)
        authorField = findViewById(R.id.authorEditText)
        descriptionField = findViewById(R.id.descriptionEditText)
        imageUrlField = findViewById(R.id.imageUrlEditText)
        copiesField = findViewById(R.id.copiesEditText)
        updateButton = findViewById(R.id.addBookButton)
    }

    private fun loadBookData(bookId: String) {
        lifecycleScope.launch {
            val book = booksService.getBook(bookId)
            if (book != null) {
                loadedBook = book
                titleField.setText(book.title)
                authorField.setText(book.author)
                descriptionField.setText(book.description)
                imageUrlField.setText(book.imagePath)
                copiesField.setText(book.copies.toString())
            } else {
                Toast.makeText(this@EditBookActivity, "Book not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleUpdateBook() {
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
                else -> {
                    val updated = booksService.updateBook(
                        loadedBook!!.copy(
                            title = titleField.toTrimString(),
                            author = authorField.toTrimString(),
                            description = descriptionField.toTrimString(),
                            imagePath = imageUrlField.toTrimString(),
                            copies = copies
                        )
                    )

                    if (updated) {
                        showToast("Book updated successfully.")
                        finish()
                    } else {
                        showToast("Update failed.")
                    }
                }
            }
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}
