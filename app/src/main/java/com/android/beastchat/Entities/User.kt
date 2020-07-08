package com.android.beastchat.Entities

data class User(
    val about: String  = "",
    val email: String = "",
    val gender: String = "",
    val userPicture: String = "",
    val username: String = "",
    val hasLoggedIn: Boolean = false
)