package com.android.beastchat.Activities

import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.RegisterFragment

class RegisterActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return RegisterFragment().newInstant()
    }

}