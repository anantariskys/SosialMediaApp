package com.example.sosialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.sosialmediaapp.R
import com.example.sosialmediaapp.adapter.PostAdapter.PostViewHolder
import com.example.sosialmediaapp.data.Post



class ProfilePostAdapter(
    private val posts: MutableList<Post>,
    private val currentUserId: String,

) : RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val contentTextView: TextView = itemView.findViewById(R.id.tvContent)
        val postImageView: ImageView = itemView.findViewById(R.id.ivPostImage)
        val likesTextView: TextView = itemView.findViewById(R.id.tvLikes)
        val commentsTextView: TextView = itemView.findViewById(R.id.tvComments)
        val likeButton: ImageButton = itemView.findViewById(R.id.btnLike)
        val commentButton: ImageButton = itemView.findViewById(R.id.btnComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.userNameTextView.text =
            post.user_id.toString()// Ideally, this would display a username if fetched

        holder.contentTextView.text = post.caption
//        holder.likesTextView.text = "${post.likes} Likes"
//        holder.commentsTextView.text = "${post.comments} Comments"

        if (!post.image.isNullOrEmpty()) {
            holder.postImageView.visibility = View.VISIBLE

        } else {
            holder.postImageView.visibility = View.GONE
        }


        // Set up like button functionality
        holder.likeButton.setOnClickListener {
            // Implement like/unlike functionality
        }

        // Set up comment button functionality
        holder.commentButton.setOnClickListener {
            // Implement comment functionality
        }

        // Fetch comments and username if required
//        fetchCommentsCount(post.id, holder)
//        fetchUserName(post.userId, holder)
    }

    private fun fetchCommentsCount(postId: String, holder: com.example.sosialmediaapp.adapter.ProfilePostAdapter.PostViewHolder) {

    }

    // Function to fetch the username from Firestore
    private fun fetchUserName(userId: String, holder: com.example.sosialmediaapp.adapter.ProfilePostAdapter.PostViewHolder) {

    }

    private fun updateLikes(postId: String, currentLikes: Int) {

    }



    // Function to unlike the post
    private fun unlikePost(postId: String, currentLikes: Int) {

    }


    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
