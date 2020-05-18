package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.beastchat.R

class FriendRequestsFragment : BaseFragments() {
    fun newInstant() : FriendRequestsFragment {
        return FriendRequestsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friend_requests, container, false)

        return rootView
    }
}