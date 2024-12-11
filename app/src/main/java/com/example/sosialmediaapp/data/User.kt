package com.example.sosialmediaapp.data

import kotlinx.serialization.Serializable



@Serializable
data class User(
    val id: String,
    val email: String,
    val profiles: Profiles
)

@Serializable
data class  Profiles(
    val id: Int,
    val user_id: String,
    val display_name: String,
    val full_name : String,
    val image :String?
)