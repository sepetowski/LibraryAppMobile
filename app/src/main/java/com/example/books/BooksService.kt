package com.example.books

import android.content.Context
import com.example.enums.UserRole
import com.example.models.LoanDisplay
import com.example.models.Book
import com.example.models.Loan
import com.example.models.Response
import com.example.user.UserService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BooksService(private val context: Context, private val userService: UserService) {

    private val db = FirebaseFirestore.getInstance()
    private val booksCollection = db.collection("books")

    private suspend fun requireAdmin(): Boolean {
        val user = userService.getCurrentUserData()
        return user?.role == UserRole.ADMIN
    }

    suspend fun addBook(book: Book): String? {
        if (!requireAdmin()) return null

        val newDoc = booksCollection.document()
        book.id = newDoc.id
        newDoc.set(book).await()
        return book.id
    }

    suspend fun updateBook(book: Book): Boolean {
        if (!requireAdmin()) return false

        return try {
            booksCollection.document(book.id).set(book).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteBook(id: String): Boolean {
        if (!requireAdmin()) return false

        return try {
            booksCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllBooks(): List<Book> {
        val snapshot = booksCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
    }

    suspend fun getBook(id: String): Book? {
        val doc = booksCollection.document(id).get().await()
        return if (doc.exists()) doc.toObject(Book::class.java) else null
    }

    suspend fun loanBookToUser(userId: String, bookId: String): Response {
        if (!requireAdmin()) return Response.NOT_ADMIN

        return try {
            val existingLoanQuery = db.collection("loans")
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("returnedAt", null)
                .get()
                .await()

            if (!existingLoanQuery.isEmpty) {
                return Response.ALREADY_LOANED
            }

            val bookDoc = booksCollection.document(bookId)
            val bookSnapshot = bookDoc.get().await()

            if (!bookSnapshot.exists()) return Response.ERROR

            val book = bookSnapshot.toObject(Book::class.java) ?: return Response.ERROR

            if (book.copies <= 0) return Response.NO_COPIES

            book.copies -= 1
            bookDoc.set(book).await()

            val newLoan = Loan(userId = userId, bookId = bookId)
            val loanRef = db.collection("loans").add(newLoan).await()

            db.collection("loans").document(loanRef.id).update("id", loanRef.id)

            Response.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            Response.ERROR
        }
    }

    suspend fun returnBookFromUser(userId: String, bookId: String): Response {
        if (!requireAdmin()) return Response.NOT_ADMIN

        return try {
            val loanQuery = db.collection("loans")
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("returnedAt", null)
                .get()
                .await()

            if (loanQuery.isEmpty) {
                return Response.ERROR
            }

            val loanDoc = loanQuery.documents.first()
            val loanId = loanDoc.id

            db.collection("loans").document(loanId).update("returnedAt", System.currentTimeMillis()).await()

            val bookDocRef = booksCollection.document(bookId)
            val bookSnapshot = bookDocRef.get().await()
            if (bookSnapshot.exists()) {
                val book = bookSnapshot.toObject(Book::class.java)
                if (book != null) {
                    book.copies += 1
                    bookDocRef.set(book).await()
                }
            }

            Response.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            Response.ERROR
        }
    }

    suspend fun getActiveLoansWithBookAndUser(): List<LoanDisplay> {
        val currentUser = userService.getCurrentUserData() ?: return emptyList()
        val isAdmin = currentUser.role == UserRole.ADMIN

        val query = if (isAdmin) {
            db.collection("loans").whereEqualTo("returnedAt", null)
        } else {
            db.collection("loans")
                .whereEqualTo("userId", currentUser.id)
                .whereEqualTo("returnedAt", null)
        }

        val loansSnapshot = query.get().await()
        val loans = loansSnapshot.documents.mapNotNull { it.toObject(Loan::class.java) }

        return loans.mapNotNull { loan ->
            val book = getBook(loan.bookId)
            val user = userService.getUserById(loan.userId)
            if (book != null && user != null) {
                LoanDisplay(
                    loanId = loan.id,
                    userId = loan.userId,
                    userNickname = user.nickname,
                    bookId = loan.bookId,
                    bookTitle = book.title,
                    imagePath = book.imagePath,
                    timestamp = loan.timestamp,
                    returnedAt = loan.returnedAt
                )
            } else null
        }
    }

    suspend fun getCompletedLoansWithBookAndUser(): List<LoanDisplay> {
        val currentUser = userService.getCurrentUserData() ?: return emptyList()
        val isAdmin = currentUser.role == UserRole.ADMIN

        val query = if (isAdmin) {
            db.collection("loans").whereNotEqualTo("returnedAt", null)
        } else {
            db.collection("loans")
                .whereEqualTo("userId", currentUser.id)
                .whereNotEqualTo("returnedAt", null)
        }

        val loansSnapshot = query.get().await()
        val loans = loansSnapshot.documents.mapNotNull { it.toObject(Loan::class.java) }

        val result = mutableListOf<LoanDisplay>()

        for (loan in loans) {
            val book = getBook(loan.bookId)
            val user = userService.getUserById(loan.userId)

            if (book != null && user != null) {
                result.add(
                    LoanDisplay(
                        loanId = loan.id,
                        userId = loan.userId,
                        userNickname = user.nickname,
                        bookId = loan.bookId,
                        bookTitle = book.title,
                        imagePath = book.imagePath,
                        timestamp = loan.timestamp,
                        returnedAt = loan.returnedAt
                    )
                )
            }
        }

        return result
    }
}
