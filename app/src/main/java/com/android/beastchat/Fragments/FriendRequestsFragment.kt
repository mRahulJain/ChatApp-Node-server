package com.android.beastchat.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.android.beastchat.Views.FriendRequestViews.FriendRequestsAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class FriendRequestsFragment : BaseFragments(), FriendRequestsAdapter.OnOptionListener {
    fun newInstant() : FriendRequestsFragment {
        return FriendRequestsFragment()
    }

    @BindView(R.id.fragment_friend_requests_recyclerView)
    lateinit var mRecyclerView: RecyclerView

    @BindView(R.id.fragment_friend_requests_noResults)
    lateinit var mNoResults : TextView

    lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mGetAllFriendRequestsReference : DatabaseReference
    private lateinit var mGetAllFriendReuestsListener: ValueEventListener
    private lateinit var mUnbinder: Unbinder
    private lateinit var mUserEmailString: String
    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")!!
        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friend_requests, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)

        var adapter = FriendRequestsAdapter(
            activity as BaseFragmentActivity,
            this
        )
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mGetAllFriendRequestsReference = FirebaseDatabase.getInstance().getReference()
            .child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mGetAllFriendReuestsListener = mLiveFriendsServices.getAllFriendRequests(adapter, mRecyclerView, mNoResults)
        mGetAllFriendRequestsReference.addValueEventListener(mGetAllFriendReuestsListener)
        mRecyclerView.adapter = adapter
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
        mGetAllFriendRequestsReference.removeEventListener(mGetAllFriendReuestsListener)
    }

    override fun onOptionClicked(user: User, result: String) {
        if(result == "0") {
            val userFriendReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(constants().FIREBASE_PATH_USER_FRIENDS)
                .child(constants().encodeEmail(mUserEmailString))
                .child(constants().encodeEmail(user.email))
            userFriendReference.setValue(user)
            mCompositeDisposable.add(
                mLiveFriendsServices.approveDeclineFriendRequest(
                    mSocket,
                    mUserEmailString,
                    user!!.email,
                    "0"
                )
            )
            mGetAllFriendRequestsReference.child(constants().encodeEmail(user.email))
                .removeValue()
        } else {
            mCompositeDisposable.add(
                mLiveFriendsServices.approveDeclineFriendRequest(
                    mSocket,
                    mUserEmailString,
                    user!!.email,
                    "1"
                )
            )
            mGetAllFriendRequestsReference.child(constants().encodeEmail(user.email))
                .removeValue()
        }
    }
}