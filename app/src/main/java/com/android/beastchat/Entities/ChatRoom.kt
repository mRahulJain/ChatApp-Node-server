package com.android.beastchat.Entities

data class ChatRoom(
    val friendPicture: String = "",
    val friendName: String = "",
    val friendEmail: String = "",
    val lastMessage: String = "",
    val lastMessageSenderEmail: String = "",
    val lastMessageRead: Boolean = false,
    val sentLastMessage: Boolean = false
)