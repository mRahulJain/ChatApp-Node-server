package com.android.beastchat.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.RegisterActivity
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class LoginFragment : BaseFragments() {
    @BindView(R.id.fragment_login_userEmail) lateinit var mUserEmail : EditText
    @BindView(R.id.fragment_login_userPassword) lateinit var mPassword : EditText
    @BindView(R.id.fragment_login_login_button) lateinit var mLoginButton : Button
    @BindView(R.id.fragment_login_register_button) lateinit var mRegisterButton : Button
    @BindView(R.id.fragment_login_animateLogin) lateinit var mAnimateLogin: LinearLayout

    private lateinit var mUnbinder: Unbinder
    private lateinit var mSocket: io.socket.client.Socket
    private var mActivity : BaseFragmentActivity? = null
    private lateinit var mLiveAccountServices: LiveAccountServices

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
        mLiveAccountServices = LiveAccountServices().getInstant()
        mSocket.connect()
        mSocket.on("token", tokenListener())
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

    @OnClick(R.id.fragment_login_login_button)
    fun semmLoginButton() {
        mAnimateLogin.isVisible = true
        mLoginButton.isVisible = false
        mCompositeDisposable.add(
            mLiveAccountServices.sendLoginInfo(
                mUserEmail,
                mPassword,
                mSocket,
                mActivity,
                mLoginButton,
                mAnimateLogin
            )
        )
    }

    @OnClick(R.id.fragment_login_register_button)
    fun setmRegisterButton() {
        startActivity(Intent(activity, RegisterActivity::class.java))
    }

    private fun tokenListener() : Emitter.Listener {
        return Emitter.Listener {
            val jsonObject = it[0] as JSONObject
            mCompositeDisposable.add(
                mLiveAccountServices.getAuthToken(
                    jsonObject,
                    mActivity,
                    mSharedPreferences
                )
            )
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as BaseFragmentActivity
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
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