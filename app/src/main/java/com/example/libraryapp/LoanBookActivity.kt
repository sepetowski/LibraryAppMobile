package com.example.libraryapp

import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.books.BooksService
import com.example.enums.UserRole
import com.example.models.Book
import com.example.models.LoanResult
import com.example.models.User
import com.example.user.UserService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoanBookActivity : BaseActivity() {

    private lateinit var userService: UserService
    private lateinit var booksService: BooksService

    private lateinit var filterUserInput: TextInputEditText
    private lateinit var userSpinner: Spinner
    private lateinit var loanButton: Button
    private lateinit var filterBookInput: TextInputEditText
    private lateinit var bookSpinner: Spinner

    private var allBooks: List<Book> = emptyList()
    private var filteredBooks: List<Book> = emptyList()
    private lateinit var bookAdapter: ArrayAdapter<String>

    private var allUsers: List<User> = emptyList()
    private var filteredUsers: List<User> = emptyList()
    private lateinit var userAdapter: ArrayAdapter<String>

    private var selectedUser: User? = null
    private var selectedBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_book)

        userService = UserService(this)
        booksService = BooksService(this, userService)

        filterUserInput = findViewById(R.id.filterUserInput)
        userSpinner = findViewById(R.id.userSpinner)
        loanButton = findViewById(R.id.loanBookButton)
        filterBookInput = findViewById(R.id.filterBookInput)
        bookSpinner = findViewById(R.id.bookSpinner)

        loanButton.isEnabled = false

        loadUsers()
        loadBooks()

        filterBookInput.addTextChangedListener {
            filterBooks(it.toString())
        }

        filterBookInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {
                filterBooks(filterBookInput.text.toString())
                true
            } else {
                false
            }
        }

        bookSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedBook = if (filteredBooks.isNotEmpty()) filteredBooks[position] else null
                checkIfReady()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBook = null
                checkIfReady()
            }
        }

        filterUserInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {
                filterUsers(filterUserInput.text.toString())
                true
            } else {
                false
            }
        }

        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedUser = if (filteredUsers.isNotEmpty()) filteredUsers[position] else null
                checkIfReady()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedUser = null
                checkIfReady()
            }
        }

        loanButton.setOnClickListener {
            selectedUser?.let { user ->
                selectedBook?.let { book ->
                    if (book.copies <= 0) {
                        Toast.makeText(this, "Book is not available", Toast.LENGTH_SHORT).show()
                    } else {
                        loanBookToUser(user, book)
                    }
                }
            }
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            allUsers = userService.getAllUsers().filter { it.role != UserRole.ADMIN }
            filterUsers("")
        }
    }

    private fun filterUsers(query: String) {
        filteredUsers = if (query.isBlank()) {
            allUsers
        } else {
            allUsers.filter { it.nickname.contains(query, ignoreCase = true) }
        }

        val displayList = filteredUsers.map { "${it.nickname} (${it.email})" }
        userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = userAdapter

        selectedUser = if (filteredUsers.isNotEmpty()) filteredUsers[0] else null
        checkIfReady()
    }

    private fun loanBookToUser(user: User, book: Book) {
        lifecycleScope.launch {
            when (val result = booksService.loanBookToUser(user.id, book.id)) {
                LoanResult.SUCCESS -> Toast.makeText(this@LoanBookActivity, "Loan successful", Toast.LENGTH_SHORT).show()
                LoanResult.ALREADY_LOANED -> Toast.makeText(this@LoanBookActivity, "User already loaned this book", Toast.LENGTH_SHORT).show()
                LoanResult.NO_COPIES -> Toast.makeText(this@LoanBookActivity, "Book is not available", Toast.LENGTH_SHORT).show()
                LoanResult.NOT_ADMIN -> Toast.makeText(this@LoanBookActivity, "Admin access required", Toast.LENGTH_SHORT).show()
                LoanResult.ERROR -> Toast.makeText(this@LoanBookActivity, "Loan failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfReady() {
        loanButton.isEnabled = selectedUser != null && selectedBook != null
    }

    private fun loadBooks() {
        lifecycleScope.launch {
            allBooks = booksService.getAllBooks()
            filterBooks("")
        }
    }

    private fun filterBooks(query: String) {
        filteredBooks = if (query.isBlank()) {
            allBooks
        } else {
            allBooks.filter { it.title.contains(query, ignoreCase = true) }
        }

        val bookDisplayList = filteredBooks.map { "${it.title} by ${it.author}" }
        bookAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookDisplayList)
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bookSpinner.adapter = bookAdapter

        selectedBook = if (filteredBooks.isNotEmpty()) filteredBooks[0] else null
        checkIfReady()
    }
}