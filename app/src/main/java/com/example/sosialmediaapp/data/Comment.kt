package com.example.sosialmediaapp.data

import com.google.firebase.Timestamp

data class Comment(
    val userName: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null
)

