package com.example.sosialmediaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sosialmediaapp.adapter.PostAdapter
import com.example.sosialmediaapp.data.Post
import com.example.sosialmediaapp.data.Profiles
import com.example.supabaseapp.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var postsAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rvPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize PostAdapter with empty list
        postsAdapter = PostAdapter(mutableListOf(), "currentUserId")
        recyclerView.adapter = postsAdapter

        val btnEditProfile = findViewById<ImageView>(R.id.ivProfileImage)
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val btnCreatePost = findViewById<ImageButton>(R.id.btnCreatePost)
        btnCreatePost.setOnClickListener {
            val intent = Intent(this, PostingActivity::class.java)
            startActivity(intent)
        }
        loadUserProfileImage()
        loadPosts()

    }

    override fun onResume() {
        super.onResume()
        loadUserProfileImage()
        loadPosts()

    }

    private fun loadUserProfileImage() {
        val ivProfile = findViewById<ImageView>(R.id.ivProfileImage)
        val userCredential = supabase.auth.currentUserOrNull()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MainActivity", "Fetching profile for user ID: ${userCredential?.id}")
                if (userCredential?.id != null) {
                    val profiles = supabase.postgrest.from("Profiles").select(columns = Columns.ALL) {
                        filter { eq("user_id", userCredential.id) }
                    }.decodeSingleOrNull<Profiles>()

                    Log.d("MainActivity", "Fetched profile data: $profiles")
                    withContext(Dispatchers.Main) {
                        profiles?.image?.let {
                            ivProfile.load(it)
                        } ?: ivProfile.setImageResource(R.drawable.ic_person)
                    }
                } else {
                    Log.d("MainActivity", "User credential is null or ID is missing")
                    withContext(Dispatchers.Main) {
                        ivProfile.setImageResource(R.drawable.ic_person)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching user profile", e)
            }
        }
    }


    private fun loadPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val columns = Columns.raw(
                    "*"
                )
                val posts = supabase.postgrest
                    .from("Posts")
                    .select(columns=columns)
                    .decodeList<Post>()

                Log.d("MainActivity", "Fetched posts: $posts")

                withContext(Dispatchers.Main) {
                    postsAdapter.updatePosts(posts)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching posts", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error fetching posts: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
