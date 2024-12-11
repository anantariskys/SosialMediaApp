package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supabaseapp.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class PostingActivity : AppCompatActivity() {

    private var imageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        val ivPreview = findViewById<ImageView>(R.id.ivPreview)
        val etContent = findViewById<EditText>(R.id.etContent)
        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val btnPost = findViewById<Button>(R.id.btnPost)


        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1001)
        }


        btnPost.setOnClickListener {
            val content = etContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, "Tulis sesuatu!", Toast.LENGTH_SHORT).show()
            } else {
                postContent(content)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.ivPreview).setImageURI(imageUri)
        }
    }

    private fun postContent(content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = imageUri?.let { uploadImageToSupabase(it) }

                val postPayload = mapOf(
                    "caption" to content,
                    "image" to imageUrl,
                    "user_id" to supabase.auth.currentUserOrNull()?.id
                )

                supabase.postgrest.from("Posts").insert(postPayload)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PostingActivity, "Posting berhasil!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("PostingActivity", "Error posting content", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PostingActivity, "Gagal memposting konten: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun uploadImageToSupabase(uri: Uri): String? {
        return try {
            val fileName = "images/${UUID.randomUUID()}.jpg"
            supabase.storage["postImg"].upload(fileName,getFileBytesFromUri(uri) )
            supabase.storage["postImg"].publicUrl(fileName)
        } catch (e: Exception) {
            Log.e("PostingActivity", "Error uploading image", e)
            null
        }
    }
    private fun getFileBytesFromUri(uri: Uri): ByteArray {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }

            return byteArrayOutputStream.toByteArray()
        } ?: throw IllegalArgumentException("Failed to open URI")
    }
}
