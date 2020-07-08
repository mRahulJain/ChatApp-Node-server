package com.android.beastchat.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.OtherProfileFragment

class OtherProfileActivity : BaseFragmentActivity() {

    var email: String = ""

    override fun createFragment(): Fragment {
        return OtherProfileFragment().newInstant()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = intent.getStringExtra("email")

        supportActionBar!!.hide()
    }

}