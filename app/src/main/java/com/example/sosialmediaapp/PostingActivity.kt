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


class PostingActivity : AppCompatActivity() {

    private var imageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)



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

    }

    private fun savePostToFirestore(postId: String, content: String, imageUrl: String?, userId: String, userName: String) {

    }
}
