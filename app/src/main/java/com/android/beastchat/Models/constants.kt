package com.android.beastchat.Models

import com.android.beastchat.Entities.User

class constants {
    val IP_LOCALHOST: String = "http://192.168.43.193:3000/"
    val USER_INFO_PREFERENCE: String = "USER_INFO_PREFERENCE"
    val USER_EMAIL : String = "USER_EMAIL"
    val USER_NAME : String = "USER_NAME"
    val USER_PICTURE : String = "USER_PICTURE"

    val FIREBASE_USERS_PATH = "users"
    val FIREBASE_FRIEND_REQUEST_SENT_PATH = "friendRequestSent"
    val FIREBASE_FRIEND_REQUEST_RECEIVED_PATH = "friendRequestReceived"
    val FIREBASE_PATH_USER_FRIENDS = "userFriends"
    val FIREBASE_PATH_USERTOKEN = "userToken"

    fun encodeEmail(email : String?) : String {
        return email!!.replace(".", ",")
    }

    fun isIncludedInMap(userHashMap : HashMap<String, User>, user: User) : Boolean {
        return userHashMap!= null && userHashMap.size != 0 &&
                userHashMap.containsKey(user.email)
    }

}