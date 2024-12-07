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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ProfilePostAdapter(
    private val posts: MutableList<Post>,
    private val currentUserId: String,
    private val firestore: FirebaseFirestore
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
        holder.userNameTextView.text = post.userId // Ideally, this would display a username if fetched

        holder.contentTextView.text = post.content
        holder.likesTextView.text = "${post.likes} Likes"
        holder.commentsTextView.text = "${post.comments} Comments"

        if (!post.imageUrl.isNullOrEmpty()) {
            holder.postImageView.visibility = View.VISIBLE
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .centerCrop()
                .into(holder.postImageView)
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
        firestore.collection("posts").document(postId)
            .collection("comments") // Assuming each post has a "comments" sub-collection
            .get()
            .addOnSuccessListener { commentsSnapshot ->
                val commentsCount = commentsSnapshot.size() // Get the count of comments
                holder.commentsTextView.text = "$commentsCount comments"
            }
            .addOnFailureListener { e ->
                Log.e("PostAdapter", "Error fetching comments count", e)
                holder.commentsTextView.text = "0 comments" // Fallback if there's an error
            }
    }

    // Function to fetch the username from Firestore
    private fun fetchUserName(userId: String, holder: com.example.sosialmediaapp.adapter.ProfilePostAdapter.PostViewHolder) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val userName = document.getString("name") // Adjust based on your user field
                holder.userNameTextView.text = userName ?: "Unknown User"
            }
            .addOnFailureListener {
                holder.userNameTextView.text = "Error fetching name"
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


    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
