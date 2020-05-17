package com.android.beastchat.Activities

import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.InboxFragment

class InboxActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return InboxFragment().newInstant()
    }

}