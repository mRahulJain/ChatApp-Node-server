package com.android.beastchat.Views.ChatRoomViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.R

class ChatRoomAdapter() : RecyclerView.Adapter<ChatRoomViewHolder>() {
    private lateinit var mActivity : BaseFragmentActivity
    private lateinit var mChatRooms : ArrayList<ChatRoom>
    private lateinit var mInflater : LayoutInflater
    private lateinit var mListener : ChatRoomListener
    private lateinit var mCurrentUserEmailString: String

    constructor(mActivity: BaseFragmentActivity, mListener: ChatRoomListener, mCurrentUserEmailString: String) : this() {
        this.mActivity = mActivity
        this.mChatRooms = arrayListOf()
        this.mInflater = mActivity.layoutInflater
        this.mListener = mListener
        this.mCurrentUserEmailString = mCurrentUserEmailString
    }

    fun setmChatRooms(chatRooms: List<ChatRoom>) {
        mChatRooms.clear()
        mChatRooms.addAll(chatRooms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = mInflater.inflate(R.layout.list_chat_room, parent, false)
        val chatRoomViewHolder = ChatRoomViewHolder(view)

        chatRoomViewHolder!!.itemView.setOnClickListener {
            val chatRoom = chatRoomViewHolder.itemView.tag as ChatRoom
            mListener.onChatRoomClicked(chatRoom)
        }

        return chatRoomViewHolder
    }

    override fun getItemCount(): Int {
        return mChatRooms.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.populate(mActivity, mChatRooms[position], mCurrentUserEmailString)
    }

    interface ChatRoomListener {
        fun onChatRoomClicked(chatRoom: ChatRoom)
    }
}