package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.beastchat.R

class UserFriendsFragment : BaseFragments() {
    fun newInstant() : UserFriendsFragment {
        return UserFriendsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_friends, container, false)

        return rootView
    }
}