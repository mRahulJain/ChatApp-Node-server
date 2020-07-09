package com.android.beastchat.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices

class EmailVerificationDialogFragment(mActivity: BaseFragmentActivity) : Dialog(mActivity) {

    private var mIsVerified: Boolean = false
    private lateinit var mUnbinder: Unbinder

    @BindView(R.id.dialog_email_verification_text)
    lateinit var mShortText: TextView
    @BindView(R.id.dialog_email_verification_details)
    lateinit var mLongText: TextView
    @BindView(R.id.dialog_email_verification_iconType)
    lateinit var mIconType: ImageView
    @BindView(R.id.dialog_email_verification_verify)
    lateinit var mVerify: Button

    constructor(mActivity: BaseFragmentActivity, mIsVerified: Boolean) : this(mActivity) {
        this.mIsVerified = mIsVerified
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_email_verification)
        mUnbinder = ButterKnife.bind(this)

        setView(mIsVerified)
    }

    private fun setView(mIsVerified: Boolean) {
        if(mIsVerified) {
            mIconType.setImageResource(R.drawable.ic_verified_user_black)
            mShortText.text = "Yayyy!!"
            mLongText.text = "You are a verified user."
            mVerify.isVisible = false
        } else {
            mIconType.setImageResource(R.drawable.ic_error)
            mShortText.text = "Confirm your email"
            mLongText.text = "Send a verification mail to verify your account?"
            mVerify.isVisible = true
        }
    }

    @OnClick(R.id.dialog_email_verification_verify)
    fun setmVerify() {
        LiveFriendsServices().sendVerificationMail(context, mLongText, mVerify)
    }
}