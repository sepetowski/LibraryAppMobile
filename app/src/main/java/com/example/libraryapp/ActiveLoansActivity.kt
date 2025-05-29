package com.example.libraryapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.books.LoanAdapter
import com.example.books.BooksService
import com.example.enums.UserRole
import com.example.models.LoanDisplay
import com.example.user.UserService
import kotlinx.coroutines.launch

class ActiveLoansActivity : BaseActivity() {

    private lateinit var noLoansTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksService: BooksService
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_loans)

        noLoansTextView = findViewById(R.id.noLoansTextView)
        recyclerView = findViewById(R.id.activeLoansRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userService = UserService(this)
        booksService = BooksService(this, userService)
        fetchActiveLoans()
    }

    private fun fetchActiveLoans() {
        lifecycleScope.launch {
            val currentUser = userService.getCurrentUserData()
            val isAdmin = currentUser?.role == UserRole.ADMIN

            val activeLoans = booksService.getActiveLoansWithBookAndUser()

            if (activeLoans.isEmpty()) {
                noLoansTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noLoansTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter = LoanAdapter(
                    loans = activeLoans,
                    showReturnButton = isAdmin,
                    isAdmin = isAdmin,
                    onReturnClicked = { loan -> returnBook(loan) }
                )
            }
        }
    }

    private fun returnBook(loan: LoanDisplay) {
        lifecycleScope.launch {
            val result = booksService.returnBookFromUser(loan.userId, loan.bookId)
            if (result.name == "SUCCESS") {
                Toast.makeText(this@ActiveLoansActivity, "Book returned", Toast.LENGTH_SHORT).show()
                fetchActiveLoans()
            } else {
                Toast.makeText(this@ActiveLoansActivity, "Failed to return book", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
