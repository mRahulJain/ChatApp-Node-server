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
import com.android.beastchat.Activities.MessagesActivity
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.android.beastchat.Views.ChatRoomViews.ChatRoomAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.socket.client.IO
import java.net.URISyntaxException

class InboxFragment : BaseFragments(), ChatRoomAdapter.ChatRoomListener {

    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mUnbinder: Unbinder

    private lateinit var mAllFriendRequestsListener: ValueEventListener
    private lateinit var mAllFriendRequestsReference: DatabaseReference
    private lateinit var mUserEmailString: String

    @BindView(R.id.fragment_inbox_recyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.fragment_inbox_noMessages)
    lateinit var mNoMessages: TextView
    @BindView(R.id.fragment_inbox_loader)
    lateinit var mLoader: TextView

    private lateinit var mUserChatRoomReference: DatabaseReference
    private lateinit var mUserChatRoomListener: ValueEventListener

    private lateinit var mUsersNewMessagesReference: DatabaseReference
    private lateinit var mUserNewMessagesListener: ValueEventListener

    private lateinit var mSocket: io.socket.client.Socket

    fun newInstant() : InboxFragment {
        return InboxFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()

        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")!!

        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
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
            .getFriendRequestBottom(nav, R.id.tab_friends, activity!!)
        mAllFriendRequestsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mAllFriendRequestsReference.addValueEventListener(mAllFriendRequestsListener)

        val adapter = ChatRoomAdapter(activity!! as BaseFragmentActivity, this, mUserEmailString)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mUserChatRoomReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_CHATROOM)
            .child(constants().encodeEmail(mUserEmailString))
        mUserChatRoomListener = mLiveFriendsServices.getAllChatRooms(mRecyclerView, mNoMessages, mLoader, adapter)
        mUserChatRoomReference.addValueEventListener(mUserChatRoomListener)
        mRecyclerView.adapter = adapter

        mUsersNewMessagesReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_NEW_MESSAGES)
            .child(constants().encodeEmail(mUserEmailString))
        mUserNewMessagesListener = mLiveFriendsServices.getAllNewMessages(nav, R.id.tab_inbox, activity!!)
        mUsersNewMessagesReference.addValueEventListener(mUserNewMessagesListener)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()

        if(mAllFriendRequestsListener != null) {
            mAllFriendRequestsReference.removeEventListener(mAllFriendRequestsListener)
        }

        if(mUserChatRoomListener != null) {
            mUserChatRoomReference.removeEventListener(mUserChatRoomListener)
        }

        if(mUserNewMessagesListener != null) {
            mUsersNewMessagesReference.removeEventListener(mUserNewMessagesListener)
        }
    }

    override fun onChatRoomClicked(chatRoom: ChatRoom) {
        val friendList = arrayListOf<String>(
            chatRoom!!.friendEmail,
            chatRoom!!.friendPicture,
            chatRoom!!.friendName
        )
        val intent = MessagesActivity()!!.newInstant(activity!!, friendList)
        startActivity(intent)
        activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}