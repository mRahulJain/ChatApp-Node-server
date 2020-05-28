package com.android.beastchat.Activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.FriendsFragment
import com.android.beastchat.Models.constants

class FriendsActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return FriendsFragment().newInstant()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(
            constants().USER_INFO_PREFERENCE,
            Context.MODE_PRIVATE
        )
        supportActionBar!!.title = sharedPreferences.getString(constants().USER_NAME, "")!! + "'s Friends"
    }

}