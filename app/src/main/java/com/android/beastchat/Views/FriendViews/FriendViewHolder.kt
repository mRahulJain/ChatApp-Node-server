package com.android.beastchat.Views.FriendViews

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.User
import com.android.beastchat.R
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.list_friends_userPicture)
    lateinit var mUserPicture: RoundedImageView

    @BindView(R.id.list_friends_userName)
    lateinit var mUserName: TextView

    @BindView(R.id.list_friends_startChat)
    lateinit var mStartChat: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, user: User) {
        itemView.tag = user

        mUserName.text = user!!.username
        Picasso.with(context)
            .load(user!!.userPicture)
            .into(mUserPicture)
    }
}