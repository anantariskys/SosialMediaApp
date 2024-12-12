package com.example.sosialmediaapp.data

import kotlinx.serialization.Serializable

//import com.google.firebase.Timestamp
@Serializable
data class Comment(
    val id: Int,
    val user_id:String,
    val post_id: Int,
    val caption: String,
    val created_at: String
)

