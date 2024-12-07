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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostAdapter(
    private var posts: MutableList<Post>,
    private val currentUserId: String,
    private val firestore: FirebaseFirestore
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

        // Set the content text
        holder.content.text = post.content

        // Load image using Picasso
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .centerCrop()
                .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }

        // Set the likes count
        holder.likesTextView.text = "${post.likes} likes"
        fetchCommentsCount(post.id, holder)

        // Fetch the username based on userId
        fetchUserName(post.userId, holder)

        // Check if the current user has liked this post
        val hasLiked = post.likedBy.contains(currentUserId)

        // Update like button icon based on like status
        if (hasLiked) {
            holder.likeButton.setImageResource(R.drawable.ic_heart)
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_heart_outline)
        }


        // Set like button click listener
        holder.likeButton.setOnClickListener {
            if (hasLiked) {
                // Unlike the post
                unlikePost(post.id, post.likes)
            } else {
                // Like the post
                updateLikes(post.id, post.likes)
            }
        }

        holder.btnComment.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.id)  // Pass the post ID to the CommentActivity
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = posts.size


    private fun fetchCommentsCount(postId: String, holder: PostViewHolder) {
        firestore.collection("posts").document(postId)
            .collection("comments") // Assuming each post has a "comments" sub-collection
            .get()
            .addOnSuccessListener { commentsSnapshot ->
                val commentsCount = commentsSnapshot.size() // Get the count of comments
                holder.commentCount.text = "$commentsCount comments"
            }
            .addOnFailureListener { e ->
                Log.e("PostAdapter", "Error fetching comments count", e)
                holder.commentCount.text = "0 comments" // Fallback if there's an error
            }
    }

    // Function to fetch the username from Firestore
    private fun fetchUserName(userId: String, holder: PostViewHolder) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val userName = document.getString("name") // Adjust based on your user field
                holder.userName.text = userName ?: "Unknown User"
            }
            .addOnFailureListener {
                holder.userName.text = "Error fetching name"
            }
    }

    private fun updateLikes(postId: String, currentLikes: Int) {
        Log.d("PostAdapter", "Attempting to update likes for postId: $postId")
        if (postId.isNotEmpty()) { // Validate postId
            val postRef = firestore.collection("posts").document(postId)
            postRef.update("likes", currentLikes + 1, "likedBy", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener {
                    Log.d("PostAdapter", "Successfully updated likes for postId: $postId")
                }
                .addOnFailureListener { e ->
                    Log.e("PostAdapter", "Error updating likes", e)
                }
        } else {
            Log.e("PostAdapter", "Invalid postId: $postId")
        }
    }



    // Function to unlike the post
    private fun unlikePost(postId: String, currentLikes: Int) {
        val postRef = firestore.collection("posts").document(postId)
        postRef.update("likes", currentLikes - 1, "likedBy", FieldValue.arrayRemove(currentUserId))
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }

    // Function to update the list of posts
    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
