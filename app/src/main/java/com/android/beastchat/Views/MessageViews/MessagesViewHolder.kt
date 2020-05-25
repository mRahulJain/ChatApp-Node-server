package com.android.beastchat.Views.MessageViews

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.Message
import com.android.beastchat.R
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

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, message: Message, mCurrentUserEmail: String) {
        if(mCurrentUserEmail != message.messageSenderEmail) {
            mUserPicture.isVisible = false
            mUserText.isVisible = false
            mFriendPicture.isVisible = true
            mFriendText.isVisible = true
            Picasso.with(context)
                .load(message.messageSenderPicture)
                .into(mFriendPicture)
            mFriendText.text = message.messageText
        } else {
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