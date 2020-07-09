package com.android.beastchat.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices

class ForgotPasswordDialog(mActivity: BaseFragmentActivity) : Dialog(mActivity) {

    private lateinit var mUnbinder: Unbinder

    @BindView(R.id.dialog_forgot_fragment_details)
    lateinit var mLongText: TextView
    @BindView(R.id.dialog_forgot_fragment_editText)
    lateinit var mEmail: EditText
    @BindView(R.id.dialog_forgot_fragment_resetPassword)
    lateinit var mReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_forgot_password)
        mUnbinder = ButterKnife.bind(this)
    }

    @OnClick(R.id.dialog_forgot_fragment_resetPassword)
    fun setmResetPassword() {
        LiveAccountServices().getInstant().sendPasswordResetLink(mEmail.text.toString())
        mLongText.text = "A reset link has been sent to\n${mEmail.text.toString()}."
        mEmail.isVisible = false
        mReset.isVisible = false
    }
}