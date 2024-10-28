package com.example.sosialmediaapp.data

data class User(
    val uid: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val followers: List<String> = emptyList()
)
