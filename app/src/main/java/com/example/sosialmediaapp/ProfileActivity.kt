package com.example.sosialmediaapp

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var profileImageView: ImageView
    private lateinit var displayNameTextView: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImageView = findViewById(R.id.ivProfileImage)
        displayNameTextView = findViewById(R.id.tvDisplayName)
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
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val displayName = document.getString("name")
                    val profileImage = document.getString("profileImage")

                    displayNameTextView.text = displayName ?: "Nama Pengguna"
                    displayNameEditText.setText(displayName)

                    profileImage?.let { imageUrl ->
                        Picasso.get().load(imageUrl).into(profileImageView)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun uploadProfileImage(uri: Uri) {
        val user = auth.currentUser
        user?.let { currentUser ->
            // Mendapatkan URL gambar profil lama
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val oldImageUrl = document.getString("profileImage")
                    val oldImageRef = oldImageUrl?.let { Uri.parse(it).lastPathSegment?.let { fileName ->
                        storage.reference.child("profile_images/$fileName")
                    } }

                    // Jika ada URL gambar profil lama, hapus gambar lama
                    if (oldImageRef != null) {
                        oldImageRef.delete().addOnSuccessListener {
                            // Upload gambar profil baru setelah menghapus yang lama
                            uploadNewProfileImage(uri, currentUser.uid)
                        }.addOnFailureListener {
                            // Jika gagal menghapus gambar lama, tetap coba untuk upload gambar baru
                            uploadNewProfileImage(uri, currentUser.uid)
                        }
                    } else {
                        // Jika tidak ada gambar profil lama, langsung upload yang baru
                        uploadNewProfileImage(uri, currentUser.uid)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get old profile image URL", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadNewProfileImage(uri: Uri, uid: String) {
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                db.collection("users").document(uid)
                    .update("profileImage", downloadUri.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDisplayName() {
        val user = auth.currentUser
        val newDisplayName = displayNameEditText.text.toString()

        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid).update("name", newDisplayName)
                .addOnSuccessListener {
                    displayNameTextView.text = newDisplayName
                    Toast.makeText(this, "Display name updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update display name", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
