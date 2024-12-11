package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var postsAdapter: PostAdapter
    private lateinit var profileImageView: ImageView
    private lateinit var fullNameEditText: EditText
    private lateinit var displayNameEditText: EditText
    private lateinit var btnSelectProfileImage: Button
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnLogout: Button

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            profileImageView.setImageURI(it)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupRecyclerView()
        loadUserData()

        btnSelectProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun initViews() {
        profileImageView = findViewById(R.id.ivProfileImage)
        fullNameEditText = findViewById(R.id.etFullName)
        displayNameEditText = findViewById(R.id.etDisplayName)
        btnSelectProfileImage = findViewById(R.id.btnSelectProfileImage)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postsAdapter = PostAdapter(mutableListOf(), "")
        recyclerView.adapter = postsAdapter
        loadPostsByUser()
    }

    private fun loadPostsByUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                user?.let {
                    val posts = supabase.postgrest
                        .from("Posts")
                        .select(columns = Columns.raw("*")) {
                            filter {
                                eq("user_id", user.id)
                            }
                        }
                        .decodeList<Post>()

                    Log.d("ProfileActivity", "Fetched posts: $posts")
                    withContext(Dispatchers.Main) {
                        postsAdapter.updatePosts(posts)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error fetching posts", e)
                withContext(Dispatchers.Main) {
                    showToast("Error fetching posts: ${e.message}")
                }
            }
        }
    }

    private fun loadUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    withContext(Dispatchers.Main) {
                        showToast("User not found")
                    }
                    return@launch
                }

                val profiles = supabase.postgrest.from("Profiles").select() {
                    filter {
                        eq("user_id", user.id)
                    }
                }.decodeSingleOrNull<Profiles>()

                withContext(Dispatchers.Main) {
                    displayNameEditText.setText(profiles?.display_name.orEmpty())
                    fullNameEditText.setText(profiles?.full_name.orEmpty())
                    profiles?.image?.let { profileImageView.load(it) } ?: profileImageView.setImageResource(R.drawable.ic_person)
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading user data", e)
                withContext(Dispatchers.Main) {
                    showToast("Error loading user data: ${e.message}")
                }
            }
        }
    }

    private fun updateProfile() {
        val displayName = displayNameEditText.text.toString().trim()
        val fullName = fullNameEditText.text.toString().trim()

        if (displayName.isEmpty() || fullName.isEmpty()) {
            showToast("Display name and full name cannot be empty")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    withContext(Dispatchers.Main) {
                        showToast("User not logged in")
                    }
                    return@launch
                }

                val profiles = supabase.postgrest.from("Profiles").select() {
                    filter {
                        eq("user_id", user.id)
                    }
                }.decodeSingleOrNull<Profiles>()

                val newImageName = "${user.id}_${UUID.randomUUID()}.jpg"
                if (selectedImageUri!==null){
                    val byteArray = getFileBytesFromUri(selectedImageUri!!)
                    supabase.storage["profileImg"].upload(newImageName, byteArray)
                    val newImageUrl = supabase.storage.from("profileImg").publicUrl(newImageName)
                    supabase.postgrest.from("Profiles").update(
                        mapOf(
                            "display_name" to displayName,
                            "full_name" to fullName,
                            "image" to newImageUrl
                        )
                    ) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        showToast("Profile updated successfully")
                        loadUserData()
                    }
                }else{
                    supabase.postgrest.from("Profiles").update(
                        mapOf(
                            "display_name" to displayName,
                            "full_name" to fullName,
                        )
                    ) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        showToast("Profile updated successfully")
                        loadUserData()
                    }
                }







            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error updating profile", e)
                withContext(Dispatchers.Main) {
                    showToast("Error updating profile: ${e.message}")
                }
            }
        }
    }

    private fun uploadNewProfileImage(uri: Uri, uid: String): String? {
        var imageUrl =""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newImageName = "${uid}_${UUID.randomUUID()}.jpg"
                val byteArray = getFileBytesFromUri(uri)
                supabase.storage["profileImg"].upload(newImageName, byteArray)
                imageUrl = supabase.storage.from("profileImg").publicUrl(newImageName)
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error uploading profile image", e)

            }
        }
        return imageUrl
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

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.auth.signOut()
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@ProfileActivity, WelcomeActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error logging out", e)
                withContext(Dispatchers.Main) {
                    showToast("Error logging out: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}