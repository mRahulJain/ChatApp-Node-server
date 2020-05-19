package com.android.beastchat.Views.FindFriendsViews

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.Fragments.FindFriendsFragment
import com.android.beastchat.R

class FindFriendsAdapter() : RecyclerView.Adapter<FindFriendsViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mUsers : ArrayList<User>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mListener : UserListener
    private lateinit var mFriendRequestSentMap : HashMap<String, User>
    private lateinit var mFriendRequestReceivedMap : HashMap<String, User>

    constructor(mActivity: BaseFragmentActivity, mListener: UserListener) : this() {
        this.mActivity = mActivity
        this.mListener = mListener
        mInflater = mActivity.layoutInflater
        mUsers = arrayListOf()
        mFriendRequestSentMap = HashMap()
        mFriendRequestReceivedMap = HashMap()
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

    fun setmUsers(users: List<User>) {
        mUsers.clear()
        mUsers.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
        val view = mInflater.inflate(R.layout.list_users, parent, false)
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
        (holder).populate(mActivity, mUsers[position], mFriendRequestSentMap, mFriendRequestReceivedMap)
    }


    interface UserListener {
        fun onUserClicked(user: User)
    }
}