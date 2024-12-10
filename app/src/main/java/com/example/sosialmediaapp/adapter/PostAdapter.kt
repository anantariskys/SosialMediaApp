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


class PostAdapter(
    private var posts: MutableList<Post>,
    private val currentUserId: String,

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tvContent)
        val image: ImageView = itemView.findViewById(R.id.ivPostImage)
        val likeButton: AppCompatImageButton = itemView.findViewById(R.id.btnLike)
        val likesTextView: TextView = itemView.findViewById(R.id.tvLikes)
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

    // Function to update the list of posts
    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
