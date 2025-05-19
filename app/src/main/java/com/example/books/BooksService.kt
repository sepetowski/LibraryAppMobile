package com.example.books

import android.content.Context
import com.example.enums.UserRole
import com.example.models.Book
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
}
