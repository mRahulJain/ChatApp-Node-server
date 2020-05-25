package com.android.beastchat.Views.MessageViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.Message
import com.android.beastchat.R

class MessagesAdapter() : RecyclerView.Adapter<MessagesViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mMessages : ArrayList<Message>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mCurrentUserEmail: String

    constructor(mActivity: BaseFragmentActivity, mCurrentUserEmail: String): this() {
        this.mActivity = mActivity
        this.mCurrentUserEmail = mCurrentUserEmail
        mMessages = arrayListOf()
        mInflater = mActivity.layoutInflater
    }

    fun setmMessages(messages: List<Message>) {
        mMessages.clear()
        mMessages.addAll(messages)
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
        holder.populate(mActivity, mMessages[position], mCurrentUserEmail)
    }

}