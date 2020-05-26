package com.android.beastchat.Views.MessageViews

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.Message
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.list_messages_friendPicture)
    lateinit var mFriendPicture: RoundedImageView
    @BindView(R.id.list_messages_friendText)
    lateinit var mFriendText: TextView
    @BindView(R.id.list_messages_userPicture)
    lateinit var mUserPicture: RoundedImageView
    @BindView(R.id.list_messages_userText)
    lateinit var mUserText: TextView
    @BindView(R.id.list_messages_seenMessage)
    lateinit var mSeenMessage: TextView
    lateinit var mLiveFriendsServices: LiveFriendsServices

    init {
        ButterKnife.bind(this, itemView)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
    }

    fun populate(context: Context, message: Message, mCurrentUserEmail: String, mFriendEmailString: String, toShowSeen: Boolean) {
        if(mCurrentUserEmail != message.messageSenderEmail) {
            mSeenMessage.isVisible = false
            mUserPicture.isVisible = false
            mUserText.isVisible = false
            mFriendPicture.isVisible = true
            mFriendText.isVisible = true
            Picasso.with(context)
                .load(message.messageSenderPicture)
                .into(mFriendPicture)
            mFriendText.text = message.messageText
        } else {
            if(toShowSeen) {
                mLiveFriendsServices.isSeenMessage(mSeenMessage, mCurrentUserEmail, mFriendEmailString)
            }
            mUserPicture.isVisible = true
            mUserText.isVisible = true
            mFriendPicture.isVisible = false
            mFriendText.isVisible = false
            Picasso.with(context)
                .load(message.messageSenderPicture)
                .into(mUserPicture)
            mUserText.text = message.messageText
        }
    }
}