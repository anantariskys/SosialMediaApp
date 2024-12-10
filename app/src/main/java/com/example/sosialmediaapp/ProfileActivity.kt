package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sosialmediaapp.adapter.PostAdapter
import com.example.sosialmediaapp.data.Post


class ProfileActivity : AppCompatActivity() {

    private lateinit var postsAdapter: PostAdapter

    private lateinit var profileImageView: ImageView
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


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        postsAdapter = PostAdapter(mutableListOf(), currentUserId, db)
        recyclerView.adapter = postsAdapter


        profileImageView = findViewById(R.id.ivProfileImage)

        displayNameEditText = findViewById(R.id.etDisplayName)
        btnSelectProfileImage = findViewById(R.id.btnSelectProfileImage)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnLogout = findViewById(R.id.btnLogout)

        loadUserData()

        btnSelectProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnUpdateProfile.setOnClickListener {
            updateDisplayName()
            selectedImageUri?.let { uri ->
                uploadProfileImage(uri)
            }
        }

        btnLogout.setOnClickListener {
//
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {

    }

    private fun uploadProfileImage(uri: Uri) {


    }


    private fun loadPosts() {

    }



    private fun uploadNewProfileImage(uri: Uri, uid: String) {

    }

    private fun updateDisplayName() {

    }
}
