package com.android.beastchat.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.RegisterFragment

class RegisterActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return RegisterFragment().newInstant()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
    }

}