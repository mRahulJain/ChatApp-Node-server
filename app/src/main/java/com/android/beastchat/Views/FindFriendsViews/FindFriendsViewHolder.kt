package com.android.beastchat.Views.FindFriendsViews

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
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

    @BindView(R.id.list_user_userStatus)
    lateinit var mUserStatus : TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun populate(context: Context, user: User, mFriendRequestSentMap : HashMap<String, User>, mFriendRequestReceivedMap : HashMap<String, User>) {
        itemView.tag = user
        mUsername.text = user!!.username
        Picasso.with(context)
            .load(user!!.userPicture)
            .into(mUserPictures)

        if(constants().isIncludedInMap(mFriendRequestSentMap, user)) {
            mAddFriend.isVisible = true
            mUserStatus.isVisible = true
            mUserStatus.text = "Friend Request Sent"
            mAddFriend.setImageResource(R.drawable.ic_close)
        } else if(constants().isIncludedInMap(mFriendRequestReceivedMap, user)) {
            mAddFriend.isVisible = false
            mUserStatus.isVisible = true
            mUserStatus.text = "This user has requested you"
        } else {
            mAddFriend.isVisible = true
            mUserStatus.isVisible = false
            mAddFriend.setImageResource(R.drawable.ic_person_add)
        }
    }
}