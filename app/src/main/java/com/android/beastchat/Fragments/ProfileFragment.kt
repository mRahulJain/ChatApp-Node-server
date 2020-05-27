package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : BaseFragments() {
    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mUnbinder: Unbinder

    private lateinit var mAllFriendRequestsListener: ValueEventListener
    private lateinit var mAllFriendRequestsReference: DatabaseReference
    private lateinit var mUserEmailString: String

    private lateinit var mUsersNewMessagesReference: DatabaseReference
    private lateinit var mUserNewMessagesListener: ValueEventListener

    fun newInstant() : ProfileFragment {
        return ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewP)
        mUnbinder = ButterKnife.bind(this, rootView)
        nav.id = R.id.tab_profile
        setUpBottomBar(nav, 3)
        nav.selectedItemId = R.id.tab_profile

        mAllFriendRequestsListener = mLiveFriendsServices
            .getFriendRequestBottom(nav, R.id.tab_friends, activity!!)
        mAllFriendRequestsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mAllFriendRequestsReference.addValueEventListener(mAllFriendRequestsListener)

        mUsersNewMessagesReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_NEW_MESSAGES)
            .child(constants().encodeEmail(mUserEmailString))
        mUserNewMessagesListener = mLiveFriendsServices.getAllNewMessages(nav, R.id.tab_inbox, activity!!)
        mUsersNewMessagesReference.addValueEventListener(mUserNewMessagesListener)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()

        if(mAllFriendRequestsListener != null) {
            mAllFriendRequestsReference.removeEventListener(mAllFriendRequestsListener)
        }

        if(mUserNewMessagesListener != null) {
            mUsersNewMessagesReference.removeEventListener(mUserNewMessagesListener)
        }
    }
}