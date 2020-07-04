package com.android.beastchat.Activities

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.MessageFragment

class MessagesActivity : BaseFragmentActivity() {
    val EXTRA_FRIEND_DETAILS = "EXTRA_FRIEND_DETAILS"

    override fun createFragment(): Fragment {
        val friendDetails = intent!!.getStringArrayListExtra(EXTRA_FRIEND_DETAILS)

//        supportActionBar!!.title = friendDetails[2]
        supportActionBar!!.hide()
        return MessageFragment().newInstant(friendDetails)
    }

    fun newInstant(context: Context, friendDetails: ArrayList<String>) : Intent {
        val intent = Intent(context, MessagesActivity::class.java)
        intent.putStringArrayListExtra(EXTRA_FRIEND_DETAILS, friendDetails)
        return intent
    }
}