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

class InboxFragment : BaseFragments() {

    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mUnbinder: Unbinder

    private lateinit var mAllFriendRequestsListener: ValueEventListener
    private lateinit var mAllFriendRequestsReference: DatabaseReference
    private lateinit var mUserEmailString: String


    fun newInstant() : InboxFragment {
        return InboxFragment()
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
        val rootView = inflater.inflate(R.layout.fragment_inbox, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewI)
        nav.id = R.id.tab_inbox
        setUpBottomBar(nav, 1)
        nav.selectedItemId = R.id.tab_inbox

        mAllFriendRequestsListener = mLiveFriendsServices
            .getFriendRequestBottom(nav, 0, activity!!)
        mAllFriendRequestsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mAllFriendRequestsReference.addValueEventListener(mAllFriendRequestsListener)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()

        if(mAllFriendRequestsListener != null) {
            mAllFriendRequestsReference.removeEventListener(mAllFriendRequestsListener)
        }
    }

}