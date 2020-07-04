package com.android.beastchat.Views.MessageViews

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.Message
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.list_messages_friendPicture)
    lateinit var mFriendPicture: RoundedImageView
    @BindView(R.id.list_messages_userPicture)
    lateinit var mUserPicture: RoundedImageView
    @BindView(R.id.list_messages_friendText)
    lateinit var mFriendText: TextView
    @BindView(R.id.list_messages_userText)
    lateinit var mUserText: TextView
    @BindView(R.id.list_messages_seenMessage)
    lateinit var mSeenMessage: ImageView
    @BindView(R.id.list_message_main_sender)
    lateinit var mainLayoutSender: LinearLayout
    @BindView(R.id.list_message_main_user)
    lateinit var mainLayoutUser: LinearLayout
    lateinit var mLiveFriendsServices: LiveFriendsServices

    init {
        ButterKnife.bind(this, itemView)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
    }

    fun populate(context: Context, message: Message, mCurrentUserEmail: String, mFriendEmailString: String, toShowSeen: Boolean) {
        if(mCurrentUserEmail != message.messageSenderEmail) {
            mFriendText.setBackgroundResource(R.drawable.message_background_sender)
            mainLayoutUser.setBackgroundResource(0)
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
            mainLayoutSender.setBackgroundResource(0)
            mainLayoutUser.setBackgroundResource(R.drawable.message_background)
            if(toShowSeen) {
                mSeenMessage.visibility = View.VISIBLE
                mLiveFriendsServices.isSeenMessage(mSeenMessage, mCurrentUserEmail, mFriendEmailString)
            } else {
                mSeenMessage.visibility = View.GONE
            }
            mUserText.isVisible = true
            mUserPicture.isVisible = true
            mFriendPicture.isVisible = false
            mFriendText.isVisible = false
            Picasso.with(context)
                .load(message.messageSenderPicture)
                .into(mUserPicture)
            mUserText.text = message.messageText
        }
    }
}