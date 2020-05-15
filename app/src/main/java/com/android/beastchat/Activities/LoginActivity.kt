package com.android.beastchat.Activities

import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.LoginFragment


class LoginActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return LoginFragment().newInstant()
    }
}
