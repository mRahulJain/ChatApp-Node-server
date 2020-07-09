package com.android.beastchat.Views.ChatRoomViews

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
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
    @BindView(R.id.list_chat_room_messageSeenIndicator)
    lateinit var mMessageSeenIndicator: ImageView
    @BindView(R.id.list_chat_room_verifiedUser)
    lateinit var mVerifiedIcon: ImageView

    lateinit var mLiveFriendsServices: LiveFriendsServices

    init {
        ButterKnife.bind(this, itemView)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
    }

    fun populate(context: Context, chatRoom: ChatRoom, currentUserEmail: String) {
        itemView.tag = chatRoom

        if(chatRoom!!.friendPicture != constants().DEFAULT_USER_PICTURE) {
            Picasso.with(context)
                .load(chatRoom!!.friendPicture)
                .into(mUserPicture)
        } else {
            mUserPicture.setImageResource(R.drawable.user_image)
        }
        mUsername.text = chatRoom!!.friendName

        if(chatRoom.lastMessageSenderEmail != currentUserEmail) {
            mMessageSeenIndicator.isVisible = false
        } else {
            mMessageSeenIndicator.isVisible = true
            mLiveFriendsServices.isSeenMessage(mMessageSeenIndicator, currentUserEmail, chatRoom.friendEmail)
        }

        mLiveFriendsServices.isEmailVerified(chatRoom!!.friendEmail, mVerifiedIcon)
        var lastMessageSent = chatRoom!!.lastMessage
        if(lastMessageSent.length > 42) {
            lastMessageSent = lastMessageSent.substring(0,42)+"..."
        }
        if(!chatRoom.sentLastMessage) {
            lastMessageSent += "(Draft)"
        }
        if(chatRoom.lastMessageSenderEmail == currentUserEmail) {
            lastMessageSent = "Me: $lastMessageSent"
        } else {
            lastMessageSent = "Their: $lastMessageSent"
        }
        mMessageIndicator.isVisible = !chatRoom.lastMessageRead
        mLastMessage.text = lastMessageSent
    }
}