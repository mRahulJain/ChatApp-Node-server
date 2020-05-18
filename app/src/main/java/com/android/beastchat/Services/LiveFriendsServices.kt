package com.android.beastchat.Services

class LiveFriendsServices {
    private lateinit var mLiveFriendsServices: LiveFriendsServices

    fun getInstant(): LiveFriendsServices{
        mLiveFriendsServices = LiveFriendsServices()
        return mLiveFriendsServices
    }
}