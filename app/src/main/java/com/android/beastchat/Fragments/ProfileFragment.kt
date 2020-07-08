package com.android.beastchat.Fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.ImageActivity
import com.android.beastchat.Activities.ProfileActivity
import com.android.beastchat.Models.AndroidPermissions
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import com.android.beastchat.Services.LiveFriendsServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import io.socket.client.IO
import java.net.URISyntaxException

class ProfileFragment : BaseFragments(),
    BottomSheetChangeProfile.BottomSheetChangeProfileListener,
    BottomSheetEditProfile.BottomSheetEditProfileListener {
    private var mActivity: BaseFragmentActivity? = null
    private lateinit var mLiveFriendsServices: LiveFriendsServices
    private lateinit var mUnbinder: Unbinder

    private lateinit var mAllFriendRequestsListener: ValueEventListener
    private lateinit var mAllFriendRequestsReference: DatabaseReference
    private lateinit var mUserEmailString: String

    private lateinit var mUsersNewMessagesReference: DatabaseReference
    private lateinit var mUserNewMessagesListener: ValueEventListener

    @BindView(R.id.fragment_profile_userPicture)
    lateinit var mUserPicture: RoundedImageView
    @BindView(R.id.fragment_profile_userName)
    lateinit var mUserName: TextView
    @BindView(R.id.fragment_profile_userEmail)
    lateinit var mUserEmail: TextView
    @BindView(R.id.fragment_profile_signOut)
    lateinit var mSignOut: Button
    @BindView(R.id.fragment_profile_change_profile)
    lateinit var mChangeProfileImage: ImageView
    @BindView(R.id.fragment_profile_editProfile)
    lateinit var mEditProfile: TextView
    @BindView(R.id.fragment_profile_userAbout)
    lateinit var mAbout: TextView
    @BindView(R.id.fragment_profile_userPassword)
    lateinit var mPassword: TextView
    @BindView(R.id.fragment_profile_userGender)
    lateinit var mGender: TextView
    @BindView(R.id.fragment_profile_userFriendsCount)
    lateinit var mFriendsCount: TextView

    private var mRequestCamera = 100
    private var mRequestImage = 101
    private lateinit var mTempUri: Uri
    private lateinit var mAndroidPermissions: AndroidPermissions
    private lateinit var mExistingPassword: String

    private lateinit var mSocket: io.socket.client.Socket

    fun newInstant() : ProfileFragment {
        return ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")!!
        mAndroidPermissions = AndroidPermissions(activity!! as BaseFragmentActivity)

        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
    }

    override fun onResume() {
        super.onResume()
        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewP)
        mUnbinder = ButterKnife.bind(this, rootView)
        nav.id = R.id.tab_profile
        setUpBottomBar(nav, 3)
        nav.selectedItemId = R.id.tab_profile


        loadProfile()

        mAllFriendRequestsListener = mLiveFriendsServices
            .getFriendRequestBottom(nav, R.id.tab_friends, activity!!)
        mAllFriendRequestsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mAllFriendRequestsReference.addValueEventListener(mAllFriendRequestsListener)

        mUsersNewMessagesReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_NEW_MESSAGES)
            .child(constants().encodeEmail(mUserEmailString))
        mUserNewMessagesListener = mLiveFriendsServices.getAllNewMessages(nav, R.id.tab_inbox, activity!!)
        mUsersNewMessagesReference.addValueEventListener(mUserNewMessagesListener)

        return rootView
    }

    private fun loadProfile() {
        if(mSharedPreferences.getString(constants().USER_PICTURE, "") != constants().DEFAULT_USER_PICTURE) {
            Picasso.with(activity!!)
                .load(mSharedPreferences.getString(constants().USER_PICTURE, ""))
                .into(mUserPicture)
        } else {
            mUserPicture.setImageResource(R.drawable.user_image)
        }
        mUserEmail.text = mUserEmailString
        mUserName.text = mSharedPreferences.getString(constants().USER_NAME, "")
        mAbout.text = mSharedPreferences.getString(constants().USER_ABOUT, "")
        getPassword(mPassword)
        mLiveFriendsServices.getFriendCount(mFriendsCount, mUserEmailString)
        mGender.text = mSharedPreferences.getString(constants().USER_GENDER, "")
    }

    private fun getPassword(mPassword: TextView) {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("password")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mExistingPassword = p0.value.toString()
                mPassword.text = mExistingPassword
            }
        })
    }

    @OnClick(R.id.fragment_profile_change_profile)
    fun setmChangeProfile() {
        val bottomSheetChangeProfile = BottomSheetChangeProfile(this)
        bottomSheetChangeProfile.show(childFragmentManager, "change profile image")
    }

    @OnClick(R.id.fragment_profile_editProfile)
    fun setmEditProfile() {
        val bottomSheetEditProfile = BottomSheetEditProfile(
            this@ProfileFragment,
            mExistingPassword,
            mAbout.text.toString(),
            mUserName.text.toString(),
            mGender.text.toString()
        )
        bottomSheetEditProfile.show(childFragmentManager, "edit profile")
    }

    private fun setmGalleryImage() {
        if(!mAndroidPermissions.checkPermissionForWriteExternalStorage()) {
            mAndroidPermissions.requestPermissionForWriteExternalStorage()
        } else if(!mAndroidPermissions.checkPermissionForReadExternalStorage()) {
            mAndroidPermissions.requestPermissionForReadExternalStorage()
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Choose Image With"), mRequestImage)
        }
    }

    private fun setmCameraImage() {
        if(!mAndroidPermissions.checkPermissionForCamera()) {
            mAndroidPermissions.requestPermissionForCamera()
        } else if(!mAndroidPermissions.checkPermissionForWriteExternalStorage()) {
            mAndroidPermissions.requestPermissionForWriteExternalStorage()
        } else if(!mAndroidPermissions.checkPermissionForReadExternalStorage()) {
            mAndroidPermissions.requestPermissionForReadExternalStorage()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            mTempUri = getOutputFile()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri)
            startActivityForResult(intent, mRequestCamera)
        }
    }

    private fun setmRemoveImage() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("userPicture")

        LiveAccountServices().getInstant().removeProfilePhoto(
            mActivity!!,
            mUserPicture,
            databaseReference,
            mSharedPreferences
        )
    }

    @OnClick(R.id.fragment_profile_signOut)
    fun setmSignOut() {
        mSharedPreferences.edit().putString(constants().USER_NAME, "").apply()
        mSharedPreferences.edit().putString(constants().USER_EMAIL, "").apply()
        mSharedPreferences.edit().putString(constants().USER_PICTURE, "").apply()

        FirebaseAuth.getInstance().signOut()
        activity!!.finish()
    }

    @OnClick(R.id.fragment_profile_userPicture)
    fun setmUserPictureClick() {
        val imageUri = mSharedPreferences.getString(constants().USER_PICTURE, "")
        if(imageUri != constants().DEFAULT_USER_PICTURE) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("imageUri" , imageUri)
            startActivity(intent)
            activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun getOutputFile(): Uri {
        val resolver = context!!.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "ProfileImage")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/BeastChat")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        resolver.openOutputStream(uri!!)
        return uri!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == mRequestImage) {
            val selectedImageUri = data!!.data

            val databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(constants().FIREBASE_USERS_PATH)
                .child(constants().encodeEmail(mUserEmailString))
                .child("userPicture")

            val storageReference = FirebaseStorage.getInstance()
                .getReference().child("userProfilePics")
                .child(constants().encodeEmail(mUserEmailString))
                .child(selectedImageUri!!.lastPathSegment.toString())
            LiveAccountServices().getInstant().changeProfilePhoto(
                databaseReference,
                storageReference,
                selectedImageUri!!,
                mActivity!!,
                mUserEmailString,
                mUserPicture,
                mSharedPreferences
            )
        }
        if(resultCode == RESULT_OK && requestCode == mRequestCamera) {
            val selectedImageUri = mTempUri

            val databaseReference = FirebaseDatabase.getInstance()
                .getReference().child(constants().FIREBASE_USERS_PATH)
                .child(constants().encodeEmail(mUserEmailString))
                .child("userPicture")

            val storageReference = FirebaseStorage.getInstance()
                .getReference().child("userProfilePics")
                .child(constants().encodeEmail(mUserEmailString))
                .child(selectedImageUri!!.lastPathSegment.toString())
            LiveAccountServices().getInstant().changeProfilePhoto(
                databaseReference,
                storageReference,
                selectedImageUri!!,
                mActivity!!,
                mUserEmailString,
                mUserPicture,
                mSharedPreferences
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()

        if(mAllFriendRequestsListener != null) {
            mAllFriendRequestsReference.removeEventListener(mAllFriendRequestsListener)
        }
        if(mUserNewMessagesListener != null) {
            mUsersNewMessagesReference.removeEventListener(mUserNewMessagesListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity!! as BaseFragmentActivity
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    override fun onButtonClickedChangeProfile(flag: Int) {
        when (flag) {
            0 -> {
                setmRemoveImage()
            }
            1 -> {
                setmCameraImage()
            }
            else -> {
                setmGalleryImage()
            }
        }
    }

    override fun onButtonClickedEditProfile(
        mAbout: String,
        mUsername: String,
        mPassword: String,
        mGender: String
    ) {
        val refAbout = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("about")
        val refUsername = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("username")
        val refPassword = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("password")
        val refGender = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_USERS_PATH)
            .child(constants().encodeEmail(mUserEmailString))
            .child("gender")

        val list = ArrayList<String>()
        list.add(mAbout)
        list.add(mUsername)
        list.add(mPassword)
        list.add(mGender)

        LiveAccountServices().getInstant().editProfile(
            refAbout, refUsername, refPassword, refGender,
            list, mSharedPreferences
        )

        loadProfile()
    }
}