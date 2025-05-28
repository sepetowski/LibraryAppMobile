package com.example.libraryapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.books.LoanAdapter
import com.example.books.BooksService
import com.example.enums.UserRole
import com.example.user.UserService
import kotlinx.coroutines.launch

class LoanHistoryActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noHistoryTextView: TextView
    private lateinit var booksService: BooksService
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_history)

        recyclerView = findViewById(R.id.loanHistoryRecyclerView)
        noHistoryTextView = findViewById(R.id.noHistoryTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userService = UserService(this)
        booksService = BooksService(this, userService)

        fetchLoanHistory()
    }

    private fun fetchLoanHistory() {
        lifecycleScope.launch {
            val currentUser = userService.getCurrentUserData()
            val isAdmin = currentUser?.role == UserRole.ADMIN

            val loans = booksService.getCompletedLoansWithBookAndUser()

            if (loans.isEmpty()) {
                noHistoryTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noHistoryTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter = LoanAdapter(
                    loans,
                    showReturnButton = false,
                    isAdmin = isAdmin,
                    onReturnClicked = {}
                )
            }
        }
    }
}
