package com.android.beastchat.Entities

data class User(
    val email: String = "",
    val userPicture: String = "",
    val username: String = "",
    val hasLoggedIn: Boolean = false
)