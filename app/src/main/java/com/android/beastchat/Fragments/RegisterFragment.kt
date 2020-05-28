package com.android.beastchat.Fragments

import android.content.Context
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
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveAccountServices
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class RegisterFragment : BaseFragments() {

    @BindView(R.id.fragment_register_userName) lateinit var mUserName : EditText
    @BindView(R.id.fragment_register_userEmail) lateinit var mUserEmail : EditText
    @BindView(R.id.fragment_register_userPassword) lateinit var mPassword : EditText
    @BindView(R.id.fragment_register_LoginButton) lateinit var mLoginButton : Button
    @BindView(R.id.fragment_register_registerButton) lateinit var mRegisterButton : Button
    @BindView(R.id.fragment_register_animateRegister) lateinit var mAnimateRegister: LinearLayout

    private lateinit var mUnbinder: Unbinder
    private lateinit var mSocket: io.socket.client.Socket
    private lateinit var mLiveAccountServices: LiveAccountServices
    var mAcitvity: BaseFragmentActivity? = null

    fun newInstant() : RegisterFragment {
        return RegisterFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()
        mSocket.on("message", accountResponse())
        mLiveAccountServices = LiveAccountServices().getInstant()
    }

    @OnClick(R.id.fragment_register_registerButton)
    fun setmRegisterButton() {
        mRegisterButton.isVisible = false
        mAnimateRegister.isVisible = true
        mCompositeDisposable.add(
            mLiveAccountServices.sendRegistrationInfo(
                mUserName,
                mUserEmail,
                mPassword,
                mSocket,
                mRegisterButton,
                mAnimateRegister
            )
        )
    }

    @OnClick(R.id.fragment_register_LoginButton)
    fun setmLoginButton() {
        activity!!.finish()
    }

    private fun accountResponse() : Emitter.Listener {
        return Emitter.Listener {
            val data = it[0] as JSONObject
            mCompositeDisposable.add(
                mLiveAccountServices.registerResponse(
                    data,
                    mAcitvity,
                    mRegisterButton,
                    mAnimateRegister
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mAcitvity = context as BaseFragmentActivity
    }

    override fun onDetach() {
        super.onDetach()
        mAcitvity = null
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