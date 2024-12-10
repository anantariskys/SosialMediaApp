package com.example.sosialmediaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supabaseapp.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email,password)

            } else {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
            }


        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(emailValue: String, passwordValue: String) {
        val auth = supabase.auth
        CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            if (auth.currentUserOrNull() != null) {
                Log.d("Login", "Login berhasil: ${auth.currentUserOrNull()}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)) // Redirect ke MainActivity
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        }

    }
}
