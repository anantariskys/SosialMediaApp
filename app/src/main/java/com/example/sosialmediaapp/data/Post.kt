package com.example.sosialmediaapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int,
    val user_id:Int,
    val caption: String,
    val image: String,
)

