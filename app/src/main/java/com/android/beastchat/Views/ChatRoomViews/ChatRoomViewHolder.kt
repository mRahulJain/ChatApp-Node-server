package com.android.beastchat.Views.ChatRoomViews

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.R
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class ChatRoomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.list_chat_room_userPicture)
    lateinit var mUserPicture: RoundedImageView
    @BindView(R.id.list_chat_room_userName)
    lateinit var mUsername: TextView
    @BindView(R.id.list_chat_room_lastMessage)
    lateinit var mLastMessage: TextView
    @BindView(R.id.list_chat_room_messageIndicator)
    lateinit var mMessageIndicator: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, chatRoom: ChatRoom, currentUserEmail: String) {
        itemView.tag = chatRoom
        Picasso.with(context)
            .load(chatRoom!!.friendPicture)
            .into(mUserPicture)
        mUsername.text = chatRoom!!.friendName

        var lastMessageSent = chatRoom!!.lastMessage
        if(lastMessageSent.length > 80) {
            lastMessageSent = lastMessageSent.substring(0,80)+"..."
        }
        if(!chatRoom.sentLastMessage) {
            lastMessageSent += "(Draft)"
        }
        if(chatRoom.lastMessageSenderEmail == currentUserEmail) {
            lastMessageSent = "Me: $lastMessageSent"
        }
        mMessageIndicator.isVisible = !chatRoom.lastMessageRead
        mLastMessage.text = lastMessageSent
    }
}