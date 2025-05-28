package com.example.books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libraryapp.R
import com.example.models.LoanDisplay
import com.google.android.material.button.MaterialButton
import java.text.DateFormat

class LoanAdapter(
    private val loans: List<LoanDisplay>,
    private val showReturnButton: Boolean = true,
    private val isAdmin: Boolean = true,
    private val onReturnClicked: (LoanDisplay) -> Unit
) : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookImageView)
        val titleText: TextView = view.findViewById(R.id.titleTextView)
        val userText: TextView = view.findViewById(R.id.userTextView)
        val loanDateText: TextView = view.findViewById(R.id.loanDateTextView)
        val returnedAtText: TextView = view.findViewById(R.id.returnedAtTextView)
        val returnButton: MaterialButton = view.findViewById(R.id.returnBookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loans[position]

        holder.titleText.text = loan.bookTitle
        if (isAdmin) {
            holder.userText.visibility = View.VISIBLE
            holder.userText.text = "Borrowed by: ${loan.userNickname}"
        } else {
            holder.userText.visibility = View.GONE
        }

        holder.loanDateText.text = "Loaned at: ${
            DateFormat.getDateTimeInstance().format(loan.timestamp)
        }"

        if (loan.returnedAt != null) {
            holder.returnedAtText.visibility = View.VISIBLE
            holder.returnedAtText.text = "Returned at: ${
                DateFormat.getDateTimeInstance().format(loan.returnedAt)
            }"
        } else {
            holder.returnedAtText.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(loan.imagePath)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.bookImage)

        if (showReturnButton) {
            holder.returnButton.visibility = View.VISIBLE
            holder.returnButton.setOnClickListener {
                onReturnClicked(loan)
            }
        } else {
            holder.returnButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = loans.size
}
