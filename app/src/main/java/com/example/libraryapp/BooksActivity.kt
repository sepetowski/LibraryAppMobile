package com.example.libraryapp
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.books.BookAdapter
import com.example.books.BooksService
import com.example.enums.UserRole
import com.example.models.Book
import com.example.user.UserService
import kotlinx.coroutines.launch

class BooksActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var booksService: BooksService
    private lateinit var userService: UserService

    private val editBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        fetchBooks()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        recyclerView = findViewById(R.id.booksRecyclerView)

        userService = UserService(this)
        booksService = BooksService(this, userService)

        fetchBooks()
    }

    private fun fetchBooks() {
        lifecycleScope.launch {
            val currentUser = userService.getCurrentUserData()
            val isAdmin = currentUser?.role == UserRole.ADMIN
            val books: List<Book> = booksService.getAllBooks()

            recyclerView.adapter = BookAdapter(books, isAdmin) { book ->
                val intent = Intent(this@BooksActivity, EditBookActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                editBookLauncher.launch(intent)
            }
        }
    }
}

