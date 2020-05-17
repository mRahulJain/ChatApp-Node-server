package com.android.beastchat.Activities

import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.ProfileFragment

class ProfileActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return ProfileFragment().newInstant()
    }

}