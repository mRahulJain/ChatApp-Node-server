package com.android.beastchat.Views.FindFriendsViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.R

class FindFriendsAdapter() : RecyclerView.Adapter<FindFriendsViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mUsers : ArrayList<User>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mListener : UserListener
    private lateinit var mFriendRequestSentMap : HashMap<String, User>
    private lateinit var mFriendRequestReceivedMap : HashMap<String, User>
    private lateinit var mCurrentUserFriends : HashMap<String, User>

    constructor(mActivity: BaseFragmentActivity, mListener: UserListener) : this() {
        this.mActivity = mActivity
        this.mListener = mListener
        mInflater = mActivity.layoutInflater
        mUsers = arrayListOf()
        mFriendRequestSentMap = HashMap()
        mFriendRequestReceivedMap = HashMap()
        mCurrentUserFriends = HashMap()
    }

    fun setmFriendRequestSentMap(friendRequestSentMap : HashMap<String, User>) {
        this.mFriendRequestSentMap.clear()
        this.mFriendRequestSentMap.putAll(friendRequestSentMap)
        notifyDataSetChanged()
    }
    fun setmFriendRequestRecievedMap(friendRequestReceivedMap : HashMap<String, User>) {
        this.mFriendRequestReceivedMap.clear()
        this.mFriendRequestReceivedMap.putAll(friendRequestReceivedMap)
        notifyDataSetChanged()
    }

    fun setmCurrentUserFriends(currentUserFriends: HashMap<String, User>) {
        this.mCurrentUserFriends.clear()
        this.mCurrentUserFriends.putAll(currentUserFriends)
        notifyDataSetChanged()
    }

    fun setmUsers(users: List<User>) {
        mUsers.clear()
        mUsers.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
        val view = mInflater.inflate(R.layout.list_find_friends, parent, false)
        val findFriendsViewHolder = FindFriendsViewHolder(view)
        findFriendsViewHolder.mAddFriend.setOnClickListener {
            val user = findFriendsViewHolder.itemView.tag as User
            mListener.onUserClicked(user)
        }
        return findFriendsViewHolder
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: FindFriendsViewHolder, position: Int) {
        (holder).populate(mActivity, mUsers[position], mFriendRequestSentMap, mFriendRequestReceivedMap, mCurrentUserFriends)
    }


    interface UserListener {
        fun onUserClicked(user: User)
    }
}