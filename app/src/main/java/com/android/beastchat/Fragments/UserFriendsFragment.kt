package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.MessagesActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.android.beastchat.Views.FriendViews.FriendAdapter
import com.google.firebase.database.*

class UserFriendsFragment : BaseFragments(), FriendAdapter.userClickedListener {
    @BindView(R.id.fragment_user_friends_recyclerView)
    lateinit var mRecyclerView: RecyclerView

    @BindView(R.id.fragment_user_friends_noFriends)
    lateinit var mTextView: TextView

    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mUnbinder: Unbinder
    private lateinit var mGetAllFriendsReference : DatabaseReference
    private lateinit var mGetAllFriendsListener : ValueEventListener
    private var mUserEmailString : String? = null
    private lateinit var mAllFriends : ArrayList<User>
    private lateinit var mAdapter : FriendAdapter

    fun newInstant() : UserFriendsFragment {
        return UserFriendsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_friends, container, false)

        mUnbinder = ButterKnife.bind(this, rootView)
        mAllFriends = arrayListOf()
        mAdapter = FriendAdapter(activity as BaseFragmentActivity, this)
        mGetAllFriendsListener = mLiveFriendsServices.getAllFriendsListener(mAdapter, mRecyclerView, mTextView)
        mGetAllFriendsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_FRIENDS)
            .child(constants().encodeEmail(mUserEmailString!!))
        mGetAllFriendsReference.addValueEventListener(mGetAllFriendsListener)
        mRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        mRecyclerView.adapter = mAdapter
        return rootView
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
        if(mGetAllFriendsListener != null) {
            mGetAllFriendsReference.removeEventListener(mGetAllFriendsListener)
        }
    }

    override fun onMessageClick(user: User) {
        val friendList = arrayListOf<String>(
            user!!.email,
            user!!.userPicture,
            user!!.username
        )
        val intent = MessagesActivity()!!.newInstant(activity!!, friendList)
        startActivity(intent)
        activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}