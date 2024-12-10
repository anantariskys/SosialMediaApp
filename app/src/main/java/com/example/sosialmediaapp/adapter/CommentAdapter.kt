package com.example.sosialmediaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.R
import com.example.sosialmediaapp.data.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val commentContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        val comentTime: TextView = itemView.findViewById(R.id.tvCommentTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)  // Pastikan layout benar
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.userName.text = comment.userName
        holder.commentContent.text = comment.content


//        comment.timestamp?.let {
//            // Convert Timestamp to Date
//            val date = it.toDate() // Convert Timestamp to Date
//            // Format the date to a more readable format
//            val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
//            holder.comentTime.text= formattedDate // Set formatted date to the TextView
//        } ?: run {
//            holder.comentTime.text = "No date" // Handle null timestamp
//        }
    }



    override fun getItemCount(): Int = comments.size
}
