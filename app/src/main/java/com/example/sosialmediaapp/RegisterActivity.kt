package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supabaseapp.supabase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import java.security.spec.ECField

class RegisterActivity : AppCompatActivity() {



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)




        // Menghubungkan komponen UI
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etName = findViewById<EditText>(R.id.etName) // EditText untuk nama pengguna
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)


        btnRegister.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val name = etName.text.toString()
            if (name.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                register(email, password, name)

            } else {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
            }


        }


        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun register(emailValue: String, passwordValue: String, nameValue: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val auth = supabase.auth
            try {

                val result = auth.signUpWith(Email) {
                    email = emailValue
                    password = passwordValue
                }

                val user = supabase.auth.retrieveUserForCurrentSession(updateSession = true)


                if (user.id != null) {
                    val profile = buildJsonObject {
                        put("user_id", user.id)
                        put("full_name", name)
                        put("usernme", phone)
                    }
                    try {
                        val profileResult = supabase.postgrest.from("profiles").insert(
                            profile
                        )

                    }catch (e:Exception){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("RegisterActivity", "Error saat membuat profile", e)

                    }



                } else {
                    throw Exception("Sign-up failed, user ID not returned")
                }

                // Show success message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    finish()
                }


            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("RegisterActivity", "Error: ${e.message}", e)
            }

        }

    }
}
