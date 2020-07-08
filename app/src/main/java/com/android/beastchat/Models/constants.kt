package com.android.beastchat.Models

import com.android.beastchat.Entities.User

class constants {
    val IP_LOCALHOST: String = "https://sms-node-server.herokuapp.com/"
    val USER_INFO_PREFERENCE: String = "USER_INFO_PREFERENCE"
    val USER_EMAIL : String = "USER_EMAIL"
    val USER_NAME : String = "USER_NAME"
    val USER_PICTURE : String = "USER_PICTURE"
    val USER_ABOUT: String = "Hey there! I am using SMS application!"
    val USER_GENDER: String = "Not yet assigned"

    val FIREBASE_USERS_PATH = "users"
    val FIREBASE_FRIEND_REQUEST_SENT_PATH = "friendRequestSent"
    val FIREBASE_FRIEND_REQUEST_RECEIVED_PATH = "friendRequestReceived"
    val FIREBASE_PATH_USER_FRIENDS = "userFriends"
    val FIREBASE_PATH_USERTOKEN = "userToken"
    val FIREBASE_PATH_USER_MESSAGES = "userMessages"
    val FIREBASE_PATH_USER_NEW_MESSAGES = "userNewMessages"

    val FIREBASE_PATH_USER_CHATROOM = "userChatRoom"
    val FIREBASE_PATH_USER_ONLINE = "userStatus"

    fun encodeEmail(email : String?) : String {
        return email!!.replace(".", ",")
    }

    val DEFAULT_USER_PICTURE = "https://i7.pngguru.com/preview/527/663/825/logo-person-user-person-icon.jpg"

    fun isIncludedInMap(userHashMap : HashMap<String, User>, user: User) : Boolean {
        return userHashMap!= null && userHashMap.size != 0 &&
                userHashMap.containsKey(user.email)
    }

}