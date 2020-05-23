package com.android.beastchat.Activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.InboxFragment
import com.android.beastchat.Models.constants
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class InboxActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return InboxFragment().newInstant()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var messageToken: String? = null
        val sharedPreferences = getSharedPreferences(
            constants().USER_INFO_PREFERENCE,
            Context.MODE_PRIVATE
        )
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                Log.d("myCHECK", "INSIDE ON COMPLETE")
                if(!it.isSuccessful) {
                    Log.e("myError", "There occurred some problem")
                    return@addOnCompleteListener
                }
                messageToken = it.result!!.token
                val currentUserEmail =sharedPreferences.getString(constants().USER_EMAIL, "")
                if(messageToken != null && !currentUserEmail.equals("")) {
                    val databaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(constants().FIREBASE_PATH_USERTOKEN)
                        .child(constants().encodeEmail(currentUserEmail))
                    databaseReference.child("token").setValue(messageToken)
                }
            }
    }

}