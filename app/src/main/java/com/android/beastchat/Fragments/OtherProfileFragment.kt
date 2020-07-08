package com.android.beastchat.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.OtherProfileActivity
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import com.android.beastchat.Services.LiveFriendsServices
import com.google.firebase.database.FirebaseDatabase
import io.socket.client.IO
import java.net.URISyntaxException

class OtherProfileFragment : BaseFragments() {

    private lateinit var mSocket: io.socket.client.Socket
    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mLiveAccountServices: LiveAccountServices
    private lateinit var mUnbinder: Unbinder
    private lateinit var mUserEmailString: String

    @BindView(R.id.fragment_other_profile_close)
    lateinit var mClose: ImageView
    @BindView(R.id.fragment_other_profile_title)
    lateinit var mTitle: TextView
    @BindView(R.id.fragment_other_profile_userPicture)
    lateinit var mImageView: ImageView
    @BindView(R.id.fragment_other_profile_userAbout)
    lateinit var mAbout: TextView
    @BindView(R.id.fragment_other_profile_action)
    lateinit var mAction: ImageView
    @BindView(R.id.fragment_other_profile_relation)
    lateinit var mRelation: TextView
    @BindView(R.id.fragment_other_profile_userName)
    lateinit var mUsername: TextView
    @BindView(R.id.fragment_other_profile_userEmail)
    lateinit var mEmail: TextView
    @BindView(R.id.fragment_other_profile_userGender)
    lateinit var mGender: TextView
    @BindView(R.id.fragment_other_profile_userFriendsCount)
    lateinit var mFriendCount: TextView


    fun newInstant() : OtherProfileFragment {
        return OtherProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()

        mUserEmailString = (activity!! as OtherProfileActivity).email
        mLiveAccountServices = LiveAccountServices().getInstant()
        mLiveFriendsServices = LiveFriendsServices().getInstant()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_other_profile, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)

        assignValues()

        return rootView
    }

    private fun assignValues() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mLiveFriendsServices.getFriendDetails(
            context!!,
            databaseReference,
            mTitle,
            mImageView,
            mAbout,
            mUsername,
            mEmail,
            mGender,
            mFriendCount
        )
    }

    @OnClick(R.id.fragment_other_profile_close)
    fun setmClose() {
        activity!!.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }
}