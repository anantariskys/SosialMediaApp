package com.example.sosialmediaapp.data

data class Post(
    var id: String = "",           // Provide a default value for the id
    val content: String = "",      // Provide a default value for the content
    val imageUrl: String? = null,  // Provide a default value (null) for the imageUrl
    val userId: String = "",       // Provide a default value for the userId
    val timestamp: Long = 0,       // Provide a default value for the timestamp
    val likes: Int = 0,            // Provide a default value for the likes
    val likedBy: List<String> = listOf() // Provide a default value for the likedBy list
)

