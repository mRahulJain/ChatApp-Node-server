package com.android.beastchat.Views.MessageViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.Message
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_messages.view.*

class MessagesAdapter() : RecyclerView.Adapter<MessagesViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mMessages : ArrayList<Message>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mCurrentUserEmail: String
    private lateinit var mFriendEmailString: String

    constructor(mActivity: BaseFragmentActivity, mCurrentUserEmail: String, mFriendEmailString: String): this() {
        this.mActivity = mActivity
        this.mCurrentUserEmail = mCurrentUserEmail
        mMessages = arrayListOf()
        mInflater = mActivity.layoutInflater
        this.mFriendEmailString = mFriendEmailString
    }

    fun setmMessages(messages: List<Message>) {
        mMessages.clear()
        mMessages.addAll(messages)
        var lastReadRef = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_CHATROOM)
            .child(constants().encodeEmail(mCurrentUserEmail))
            .child(constants().encodeEmail(mFriendEmailString))
            .child("lastMessageRead")
        lastReadRef.setValue(true)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val view = mInflater.inflate(R.layout.list_messages, parent, false)
        return MessagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.itemView.list_messages_secureText.isVisible = position == 0
        if(position == mMessages.size - 1) {
            holder.populate(mActivity, mMessages[position], mCurrentUserEmail, mFriendEmailString, true)
        } else {
            holder.populate(mActivity, mMessages[position], mCurrentUserEmail, mFriendEmailString, false)
        }
    }

}