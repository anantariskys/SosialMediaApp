package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostingActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            selectImage()
        }

        findViewById<Button>(R.id.btnPost).setOnClickListener {
            val content = findViewById<EditText>(R.id.etContent).text.toString()
            if (content.isNotEmpty()) {
                uploadPost(content)
            } else {
                Toast.makeText(this, "Tulis sesuatu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.ivPreview).apply {
                visibility = View.VISIBLE
                setImageURI(imageUri)
            }
        }
    }

    private fun uploadPost(content: String) {
        val userId = auth.currentUser?.uid ?: return
        val postId = db.collection("posts").document().id

        // Get user reference from Firestore
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val userName = document.getString("userName") ?: "Anonymous" // Retrieve userName or default to "Anonymous"

                if (imageUri != null) {
                    val imageRef = storage.reference.child("posts/$postId.jpg")
                    imageRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                savePostToFirestore(postId, content, uri.toString(), userId, userName)
                            }
                        }
                } else {
                    savePostToFirestore(postId, content, null, userId, userName)
                }
            } else {
                Toast.makeText(this, "User tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil data pengguna.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePostToFirestore(postId: String, content: String, imageUrl: String?, userId: String, userName: String) {
        val post = hashMapOf(
            "content" to content,
            "imageUrl" to imageUrl,
            "userId" to userId,
            "userName" to userName, // Use the retrieved userName
            "timestamp" to System.currentTimeMillis(),
            "likes" to 0
        )

        db.collection("posts").document(postId).set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Posting berhasil!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal posting.", Toast.LENGTH_SHORT).show()
            }
    }
}
