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


class CommentActivity : AppCompatActivity() {


    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val postId = intent.getStringExtra("postId") ?: return


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

    }

    private fun loadComments(postId: String) {

    }

    private fun addComment(postId: String, content: String) {

    }}
