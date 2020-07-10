package com.android.beastchat.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.R

class PasswordDialog(mActivity: BaseFragmentActivity) : Dialog(mActivity) {

    private lateinit var mUnbinder: Unbinder

    @BindView(R.id.dialog_password_okay)
    lateinit var mOkay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_password)
        mUnbinder = ButterKnife.bind(this)
    }

    @OnClick(R.id.dialog_password_okay)
    fun setmOnClick() {
        dismiss()
    }
}