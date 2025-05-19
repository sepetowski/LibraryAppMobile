package com.example.books

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libraryapp.R
import com.example.models.Book

class BookAdapter(
    private val books: List<Book>,
    private val isAdmin: Boolean,
    private val onEditClicked: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookImageView)
        val titleText: TextView = view.findViewById(R.id.titleTextView)
        val authorText: TextView = view.findViewById(R.id.authorTextView)
        val descriptionText: TextView = view.findViewById(R.id.descriptionTextView)
        val stockText: TextView = view.findViewById(R.id.stockTextView)
        val editButton: Button = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.titleText.text = book.title
        holder.authorText.text = "by ${book.author}"
        holder.descriptionText.text = book.description

        if (book.copies <= 0) {
            holder.stockText.text = "Out of stock"
            holder.stockText.setTextColor(Color.RED)
        } else {
            holder.stockText.text = "Available copies: ${book.copies}"
            holder.stockText.setTextColor(Color.parseColor("#388E3C"))
            "Available copies: ${book.copies}"
        }

        Glide.with(holder.itemView.context)
            .load(book.imagePath)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.bookImage)

        if (isAdmin) {
            holder.editButton.visibility = View.VISIBLE
            holder.editButton.setOnClickListener {
                onEditClicked(book)
            }
        } else {
            holder.editButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = books.size
}