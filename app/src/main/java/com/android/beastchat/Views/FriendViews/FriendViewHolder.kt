package com.android.beastchat.Views.FriendViews

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.list_friends_userPicture)
    lateinit var mUserPicture: RoundedImageView

    @BindView(R.id.list_friends_userName)
    lateinit var mUserName: TextView

    @BindView(R.id.list_friends_startChat)
    lateinit var mStartChat: ImageView

    @BindView(R.id.list_friends_verifiedUser)
    lateinit var mVerifiedIcon: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, user: User) {
        itemView.tag = user

        LiveFriendsServices().getInstant().isEmailVerified(user!!.email, mVerifiedIcon)
        mUserName.text = user!!.username
        if(user!!.userPicture != constants().DEFAULT_USER_PICTURE) {
            Picasso.with(context)
                .load(user!!.userPicture)
                .into(mUserPicture)
        } else {
            mUserPicture.setImageResource(R.drawable.user_image)
        }
    }
}