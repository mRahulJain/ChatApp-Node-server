package com.android.beastchat.Activities

import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.FriendsFragment

class FriendsActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return FriendsFragment().newInstant()
    }

}