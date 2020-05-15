package com.android.beastchat.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.RegisterActivity
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import io.socket.client.IO
import java.net.URISyntaxException

class LoginFragment : BaseFragments() {
    @BindView(R.id.fragment_login_userName) lateinit var mUserName : EditText
    @BindView(R.id.fragment_login_userPassword) lateinit var mPassword : EditText
    @BindView(R.id.fragment_login_login_button) lateinit var mLoginButton : Button
    @BindView(R.id.fragment_login_register_button) lateinit var mRegisterButton : Button

    private lateinit var mUnbinder: Unbinder
    private lateinit var mSocket: io.socket.client.Socket

    fun newInstant() : LoginFragment {
        return LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        return rootView
    }

    @OnClick(R.id.fragment_login_register_button)
    fun setmRegisterButton() {
        startActivity(Intent(activity, RegisterActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}