package com.android.beastchat.Views.FindFriendsViews

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

class FindFriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.list_user_userPicture)
    lateinit var mUserPictures : RoundedImageView

    @BindView(R.id.user_list_addFriend)
    lateinit var mAddFriend : ImageView

    @BindView(R.id.list_user_username)
    lateinit var mUsername : TextView

//    @BindView(R.id.list_user_userStatus)
//    lateinit var mUserStatus : TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, user: User) {
        itemView.tag = user
        mUsername.text = user!!.username
        Picasso.with(context)
            .load(user!!.userPicture)
            .into(mUserPictures)
    }
}