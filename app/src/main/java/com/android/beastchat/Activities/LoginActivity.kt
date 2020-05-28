package com.android.beastchat.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.LoginFragment


class LoginActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return LoginFragment().newInstant()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
    }
}
