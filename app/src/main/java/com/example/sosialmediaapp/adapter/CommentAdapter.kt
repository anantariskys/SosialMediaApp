package com.example.sosialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.R
import com.example.sosialmediaapp.data.Comment
import com.example.sosialmediaapp.data.Profiles
import com.example.supabaseapp.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val commentContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        val comentTime: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)  // Pastikan layout benar
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        CoroutineScope(Dispatchers.IO).launch{
            try {
                val profiles = supabase.postgrest.from("Profiles").select(){
                    filter {
                        eq("user_id",comment.user_id)
                    }
                }.decodeSingleOrNull<Profiles>()

                holder.userName.setText(profiles?.display_name)

            }catch (e:Exception){
                Log.e("CommentAdapter", "Error fetching profile: ${e.message}")
            }
        }
        holder.userName.text = comment.user_id
        holder.commentContent.text = comment.caption


        comment.created_at?.let {

            holder.comentTime.text= formatCreatedAt(it)
        } ?: run {
            holder.comentTime.text = "No date" // Handle null timestamp
        }
    }
    fun formatCreatedAt(dateString: String): String {


        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())

        // Format output yang diinginkan
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        return try {
            // Mengonversi string ke objek Date
            val date = inputFormat.parse(dateString)

            // Jika date berhasil diparse, format dan kembalikan
            date?.let {
                outputFormat.format(it)
            } ?: "Invalid Date" // Jika parsing gagal, kembalikan "Invalid Date"
        } catch (e: Exception) {
            e.printStackTrace() // Tangani error jika ada masalah dalam parsing
            "Invalid Date" // Jika terjadi kesalahan, kembalikan "Invalid Date"
        }
    }



    override fun getItemCount(): Int = comments.size
}
