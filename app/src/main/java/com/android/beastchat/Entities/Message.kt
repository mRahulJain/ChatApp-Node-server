package com.android.beastchat.Entities

data class Message(
    val messageId: String = "",
    val messageText: String = "",
    val messageSenderEmail: String = "",
    val messageSenderPicture: String = ""
)