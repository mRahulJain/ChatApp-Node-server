package com.android.beastchat.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetEditProfile() : BottomSheetDialogFragment() {

    lateinit var mListener: BottomSheetEditProfileListener
    lateinit var mExistingPassword: String
    lateinit var about: String
    lateinit var username: String
    lateinit var gender: String

    constructor(
        mListener: BottomSheetEditProfileListener,
        mExistingPassword: String,
        mAbout: String,
        mUsername: String,
        mGender: String
    ) : this() {
        this.mListener = mListener
        this.mExistingPassword = mExistingPassword
        this.about = mAbout
        this.username = mUsername
        this.gender = mGender
    }

    private lateinit var mUnbinder: Unbinder
    @BindView(R.id.fragment_bottom_edit_profile_about)
    lateinit var mAbout: EditText
    @BindView(R.id.fragment_bottom_edit_profile_username)
    lateinit var mUsername: EditText
    @BindView(R.id.fragment_bottom_edit_profile_currentPassword)
    lateinit var mCurrentPassword: EditText
    @BindView(R.id.fragment_bottom_edit_profile_newPassword1)
    lateinit var mNewPassword: EditText
    @BindView(R.id.fragment_bottom_edit_profile_newPassword2)
    lateinit var mNewPasswordConfirm: EditText
    @BindView(R.id.fragment_bottom_edit_profile_male)
    lateinit var mMale: RadioButton
    @BindView(R.id.fragment_bottom_edit_profile_female)
    lateinit var mFemale: RadioButton
    @BindView(R.id.fragment_bottom_edit_profile_other)
    lateinit var mOther: RadioButton
    @BindView(R.id.fragment_bottom_edit_profile_updateProfile)
    lateinit var mUpdateProfile: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_edit_profile, container, false)
        mUnbinder = ButterKnife.bind(this, view)

        mAbout.setText(this.about)
        mUsername.setText(this.username)
        when (this.gender) {
            "Male" -> {
                mMale.isChecked = true
                mFemale.isChecked = false
                mOther.isChecked = false
            }
            "Female" -> {
                mMale.isChecked = false
                mFemale.isChecked = true
                mOther.isChecked = false
            }
            "Other" -> {
                mMale.isChecked = false
                mFemale.isChecked = false
                mOther.isChecked = true
            }
            else -> {
                mMale.isChecked = false
                mFemale.isChecked = false
                mOther.isChecked = false
            }
        }

        return view
    }

    @OnClick(R.id.fragment_bottom_edit_profile_updateProfile)
    fun setmUpdateProfile() {
        val about = mAbout.text.toString()
        val username = mUsername.text.toString()
        val currentPassword = mCurrentPassword.text.toString()
        var newPassword = mNewPassword.text.toString()
        val newPasswordConfirm = mNewPasswordConfirm.text.toString()
        var gender = ""

        if(newPassword != "" && newPasswordConfirm != "") {
            if(currentPassword != mExistingPassword) {
                Toast.makeText(context,"Current password is wrong", Toast.LENGTH_SHORT).show()
                return
            }

            if(newPassword != newPasswordConfirm) {
                Toast.makeText(context,"New password's do not match", Toast.LENGTH_SHORT).show()
                return
            }

            if(newPassword.length < 6) {
                Toast.makeText(context,"password is too short", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if(mMale.isChecked) {
            gender = "Male"
        }
        if(mFemale.isChecked) {
            gender = "Female"
        }
        if(mOther.isChecked) {
            gender = "Other"
        }

        if(newPassword == "" && newPasswordConfirm == "") {
            newPassword = mExistingPassword
        }

        mListener.onButtonClickedEditProfile(about, username, newPassword, gender)
        dismiss()
    }

    interface BottomSheetEditProfileListener {
        fun onButtonClickedEditProfile(
            mAbout: String,
            mUsername: String,
            mPassword: String,
            mGender: String
        )
    }
}