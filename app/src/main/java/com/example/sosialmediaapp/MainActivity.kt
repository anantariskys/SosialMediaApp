package com.example.sosialmediaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.adapter.PostAdapter
import com.example.sosialmediaapp.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var postsAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.rvPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currentUserId = auth.currentUser?.uid ?: "" // Get current user ID
        postsAdapter = PostAdapter(mutableListOf(), currentUserId, db) // Pass user ID to adapter
        recyclerView.adapter = postsAdapter

        // Initialize Edit Profile button
        val btnEditProfile = findViewById<ImageView>(R.id.ivProfileImage)
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize Create Post button
        val btnCreatePost = findViewById<ImageButton>(R.id.btnCreatePost)
        btnCreatePost.setOnClickListener {
            val intent = Intent(this, PostingActivity::class.java)
            startActivity(intent)
        }

        loadPosts()
        loadUserProfileImage()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfileImage()
        loadPosts()
    }

    private fun loadUserProfileImage() {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                val profileImageUrl = document.getString("profileImage")
                profileImageUrl?.let { url ->
                    Picasso.get().load(url).into(findViewById<ImageView>(R.id.ivProfileImage))
                } ?: run {
                    findViewById<ImageView>(R.id.ivProfileImage).setImageResource(R.drawable.circle_background)
                }
            }
        }
    }

    private fun loadPosts() {
        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    return@addSnapshotListener
                }


                val posts = snapshot?.documents?.map { document ->
                    var post = document.toObject(Post::class.java)
                    post?.id = document.id
                    post
                }?.filterNotNull() ?: return@addSnapshotListener


                postsAdapter.updatePosts(posts)
            }
    }

}
