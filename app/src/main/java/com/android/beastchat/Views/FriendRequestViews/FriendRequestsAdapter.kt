package com.android.beastchat.Views.FriendRequestViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.R

class FriendRequestsAdapter() : RecyclerView.Adapter<FriendRequestsViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mUsers : ArrayList<User>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mListener: OnOptionListener

    constructor(mActivity: BaseFragmentActivity, mListener: OnOptionListener): this() {
        this.mActivity = mActivity
        this.mListener = mListener
        this.mInflater = mActivity.layoutInflater
        this.mUsers = arrayListOf()
    }

    fun setmUsers(users: List<User>) {
        mUsers.clear()
        mUsers.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestsViewHolder {
        val view = mInflater.inflate(R.layout.list_friend_requests, parent, false)
        val friendRequestsViewHolder = FriendRequestsViewHolder(view)

        friendRequestsViewHolder.mApproveRequest.setOnClickListener {
            val user = friendRequestsViewHolder.itemView.tag as User
            mListener.onOptionClicked(user, "0")
        }

        friendRequestsViewHolder.mCancelRequest.setOnClickListener {
            val user = friendRequestsViewHolder.itemView.tag as User
            mListener.onOptionClicked(user, "1")
        }

        return friendRequestsViewHolder
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: FriendRequestsViewHolder, position: Int) {
        (holder as FriendRequestsViewHolder).populate(mActivity, mUsers[position])
    }

    interface OnOptionListener {
        fun onOptionClicked(user: User, result: String)
    }

}