package com.example.sosialmediaapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.adapter.CommentAdapter
import com.example.sosialmediaapp.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso

class CommentActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val postId = intent.getStringExtra("postId") ?: return
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        val postContent: TextView = findViewById(R.id.tvPostContent)
        val postImage: ImageView = findViewById(R.id.ivPostImage)
        val commentRecyclerView: RecyclerView = findViewById(R.id.rvComments)
        val commentInput: EditText = findViewById(R.id.etComment)
        val sendButton: Button = findViewById(R.id.btnSendComment)

        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(comments)
        commentRecyclerView.adapter = commentAdapter

        loadPost(postId, postContent, postImage)
        loadComments(postId)

        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(postId, commentText)
                commentInput.text.clear()
            } else {
                Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPost(postId: String, postContent: TextView, postImage: ImageView) {
        firestore.collection("posts").document(postId).get()
            .addOnSuccessListener { document ->
                postContent.text = document.getString("content") ?: "No Content"
                val imageUrl = document.getString("imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    postImage.visibility = View.VISIBLE
                    Picasso.get().load(imageUrl).into(postImage)
                } else {
                    postImage.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("CommentActivity", "Error loading post", e)
            }
    }

    private fun loadComments(postId: String) {
        firestore.collection("posts").document(postId).collection("comments")
            .get()
            .addOnSuccessListener { result ->
                comments.clear()
                for (document in result) {
                    val comment = document.toObject(Comment::class.java)
                    comments.add(comment)
                }
                commentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("CommentActivity", "Error loading comments", e)
            }
    }

    private fun addComment(postId: String, content: String) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: return // Return if there's no user

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val userName = document.getString("name") ?: "Unknown User"

                // Create a new comment as a HashMap
                val commentData = hashMapOf(
                    "userName" to userName,
                    "content" to content,
                    "timestamp" to FieldValue.serverTimestamp() // This is correct for Firestore
                )

                firestore.collection("posts").document(postId).collection("comments")
                    .add(commentData)
                    .addOnSuccessListener { documentReference ->
                        documentReference.get().addOnSuccessListener { doc ->
                            val comment = Comment(
                                userName = doc.getString("userName") ?: "Unknown User",
                                content = doc.getString("content") ?: "",
                                timestamp = doc.getTimestamp("timestamp") // This retrieves the Timestamp
                            )
                            comments.add(comment)
                            commentAdapter.notifyDataSetChanged()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("CommentActivity", "Error adding comment", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("CommentActivity", "Error fetching user name", e)
            }


    }}
