package com.example.sosialmediaapp

import android.annotation.SuppressLint
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
import coil.load
import com.example.sosialmediaapp.adapter.CommentAdapter
import com.example.sosialmediaapp.data.Comment
import com.example.sosialmediaapp.data.Post
import com.example.sosialmediaapp.data.Profiles
import com.example.supabaseapp.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Locale


class CommentActivity : AppCompatActivity() {


    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val postId = intent.getIntExtra("postId", 0)


        Log.e("CommentActivity", "Post ID: $postId")

        val postContent: TextView = findViewById(R.id.tvPostContent)
        val postImage: ImageView = findViewById(R.id.ivPostImage)
        val postUsername: TextView = findViewById(R.id.tvUsername)
        val postCreatedAt: TextView = findViewById(R.id.tvCreatedAt)
        val commentRecyclerView: RecyclerView = findViewById(R.id.rvComments)
        val commentInput: EditText = findViewById(R.id.etComment)
        val sendButton: Button = findViewById(R.id.btnSendComment)

        loadPost(postId.toInt(), postContent, postImage, postUsername, postCreatedAt)

        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(comments)
        commentRecyclerView.adapter = commentAdapter


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

    override fun onResume() {
        super.onResume()
        val postId = intent.getIntExtra("postId", 0)
        loadComments(postId)
    }

    private fun loadPost(
        postId: Int,
        postContent: TextView,
        postImage: ImageView,
        postUsername: TextView,
        postCreatedAt: TextView
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = supabase.postgrest.from("Posts").select(columns = Columns.ALL) {
                    filter {
                        eq("id", postId);
                    }

                }.decodeSingleOrNull<Post>()

                val profile = supabase.postgrest.from("Profiles").select(columns = Columns.ALL) {
                    filter {
                        result?.user_id?.let { eq("user_id", it) }
                    }
                }.decodeSingleOrNull<Profiles>()



                Log.d("CommentActivity", "Post result: $result")

                runOnUiThread {
                    postContent.text = result?.caption
                    if (result !== null) {
                        postCreatedAt.text = formatCreatedAt(result.created_at)
                    }
                    if (profile !== null) {
                        postUsername.text = profile.display_name
                    }

                    if (!result?.image.isNullOrEmpty()) {
                        postImage.visibility = View.VISIBLE
                        postImage.load(result?.image)
                    }
                }


            } catch (e: Exception) {
                Log.e("CommentActivity", "Error fetching post", e)
            }
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


    private fun loadComments(postId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = supabase.postgrest.from("Comments")
                    .select(columns = Columns.ALL) {
                        filter { eq("post_id", postId) }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<Comment>()

                runOnUiThread {
                    comments.clear() // Hapus data lama
                    comments.addAll(result) // Tambahkan data baru
                    commentAdapter.notifyDataSetChanged() // Beri tahu adapter bahwa datanya berubah
                }
            } catch (e: Exception) {
                Log.e("CommentActivity", "Error loading comments", e)
            }
        }
    }


    private fun addComment(postId: Int, content: String) {
        val user = supabase.auth.currentUserOrNull()
        if (user == null) {
            Log.e("CommentActivity", "User not authenticated")
            Toast.makeText(this, "Anda harus login untuk berkomentar", Toast.LENGTH_SHORT).show()
            return
        }

        val comment = buildJsonObject {
            put("user_id", user.id)
            put("post_id", postId)
            put("caption", content)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = supabase.postgrest.from("Comments").insert(comment)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CommentActivity,
                        "Komentar berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadComments(postId)

                }
            } catch (e: Exception) {
                Log.e("CommentActivity", "Error adding comment", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CommentActivity,
                        "Terjadi kesalahan saat menambahkan komentar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

