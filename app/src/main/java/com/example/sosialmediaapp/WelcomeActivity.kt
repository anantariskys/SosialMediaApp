package com.example.sosialmediaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val signUpBtn : Button = findViewById(R.id.btnSignUp)
        val loginBtn : Button = findViewById(R.id.btnLogin)

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }




    }
}