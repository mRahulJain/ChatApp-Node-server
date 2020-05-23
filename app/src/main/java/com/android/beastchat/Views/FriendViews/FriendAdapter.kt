package com.android.beastchat.Views.FriendViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.R

class FriendAdapter() : RecyclerView.Adapter<FriendViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mUsers : ArrayList<User>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mListener: userClickedListener

    constructor(mActivity: BaseFragmentActivity, mListener: userClickedListener) : this() {
        this.mActivity = mActivity
        this.mListener = mListener
        this.mUsers = arrayListOf()
        this.mInflater = mActivity.layoutInflater

    }

    fun setmUser(users: List<User>) {
        this.mUsers.clear()
        this.mUsers.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = mInflater.inflate(R.layout.list_friends, parent, false)
        val friendViewHolder = FriendViewHolder(view)

        friendViewHolder.mStartChat.setOnClickListener {
            val user = friendViewHolder.itemView.tag as User
            mListener.onMessageClick(user)
        }

        return friendViewHolder
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        (holder as FriendViewHolder).populate(mActivity, mUsers[position])
    }

    interface userClickedListener {
        fun onMessageClick(user: User)
    }

}