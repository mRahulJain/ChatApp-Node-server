package com.android.beastchat.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.ImageActivity
import com.android.beastchat.Activities.MessagesActivity
import com.android.beastchat.Activities.OtherProfileActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import com.android.beastchat.Services.LiveFriendsServices
import com.google.firebase.database.*
import io.socket.client.IO
import java.net.URISyntaxException

class OtherProfileFragment : BaseFragments() {

    private lateinit var mSocket: io.socket.client.Socket
    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mLiveAccountServices: LiveAccountServices
    private lateinit var mUnbinder: Unbinder
    private lateinit var mUserEmailString: String
    private lateinit var mUserType: String
    private var mImageURL: String = constants().DEFAULT_USER_PICTURE
    private lateinit var mGetAllFriendRequestsReference : DatabaseReference

    @BindView(R.id.fragment_other_profile_close)
    lateinit var mClose: ImageView
    @BindView(R.id.fragment_other_profile_title)
    lateinit var mTitle: TextView
    @BindView(R.id.fragment_other_profile_userPicture)
    lateinit var mImageView: ImageView
    @BindView(R.id.fragment_other_profile_userAbout)
    lateinit var mAbout: TextView
    @BindView(R.id.fragment_other_profile_action1)
    lateinit var mAction: ImageView
    @BindView(R.id.fragment_other_profile_userName)
    lateinit var mUsername: TextView
    @BindView(R.id.fragment_other_profile_userEmail)
    lateinit var mEmail: TextView
    @BindView(R.id.fragment_other_profile_userGender)
    lateinit var mGender: TextView
    @BindView(R.id.fragment_other_profile_userFriendsCount)
    lateinit var mFriendCount: TextView
    @BindView(R.id.fragment_other_profile_verifiedUser)
    lateinit var mVerifiedIcon: ImageView

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
        mUserType = (activity!! as OtherProfileActivity).type
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
        mGetAllFriendRequestsReference = FirebaseDatabase.getInstance().getReference()
            .child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))

        intializeImageURL()
        assignValues()
        return rootView
    }

    private fun intializeImageURL() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("userPicture")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mImageURL = p0.value.toString()

            }
        })
    }

    @OnClick(R.id.fragment_other_profile_action1)
    fun setmAction1() {
        activity!!.finish()
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
        mLiveFriendsServices.isEmailVerified(mUserEmailString, mVerifiedIcon)
    }

    @OnClick(R.id.fragment_other_profile_close)
    fun setmClose() {
        activity!!.finish()
    }

    @OnClick(R.id.fragment_other_profile_verifiedUser)
    fun setmOnClickVerifiedIcon() {
        Toast.makeText(context, "This user is verified", Toast.LENGTH_SHORT).show()
    }

    @OnClick(R.id.fragment_other_profile_userPicture)
    fun setmClickUserPicture() {
        if(mImageURL != constants().DEFAULT_USER_PICTURE) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("imageUri" , mImageURL)
            startActivity(intent)
            activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }
}