package com.example.sosialmediaapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.CommentActivity
import com.example.sosialmediaapp.R
import com.example.sosialmediaapp.data.Post
import com.example.sosialmediaapp.data.Profiles
import com.example.sosialmediaapp.data.User
import com.example.supabaseapp.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.toLocalTime
import java.text.SimpleDateFormat
import java.util.Locale


class PostAdapter(
    private var posts: MutableList<Post>,
    private val currentUserId: String,

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tvContent)
        val image: ImageView = itemView.findViewById(R.id.ivPostImage)
        val likeButton: AppCompatImageButton = itemView.findViewById(R.id.btnLike)
        val likesTextView: TextView = itemView.findViewById(R.id.tvLikes)
        val createdAt :TextView = itemView.findViewById(R.id.tvCreatedAt)
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val btnComment : ImageButton = itemView.findViewById(R.id.btnComment)
        val commentCount: TextView = itemView.findViewById(R.id.tvComments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]


        holder.content.text = post.caption
        val formattedDate = formatCreatedAt(post.created_at)


        holder.createdAt.text = formattedDate









        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profile = supabase.postgrest
                    .from("Profiles")
                    .select(){
                        filter {
                            eq("user_id", post.user_id)
                        }
                    }
                    .decodeSingleOrNull<Profiles>()

                withContext(Dispatchers.Main) {
                    if (profile != null) {
                        holder.userName.text = profile.display_name// Atur data user di UI
                        Log.d("PostAdapter", "Profile fetched successfully: ${profile.id}")
                    } else {
                        Log.e("PostAdapter", "Profile is null")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostAdapter", "Error fetching profile: ${e.message}", e)
            }
        }


        // Load image using Picasso
        if (!post.image.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
        } else {
            holder.image.visibility = View.GONE
        }
        holder.likeButton.setImageResource(R.drawable.ic_heart)






        holder.btnComment.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.id)  // Pass the post ID to the CommentActivity
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = posts.size


    private fun fetchCommentsCount(postId: String, holder: PostViewHolder) {

    }

    // Function to fetch the username from Firestore
    private fun fetchUserName(userId: String, holder: PostViewHolder) {

    }

    private fun updateLikes(postId: String, currentLikes: Int) {

    }



    // Function to unlike the post
    private fun unlikePost(postId: String, currentLikes: Int) {

    }
    fun formatCreatedAt(dateString: String): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

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


    // Function to update the list of posts
    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
