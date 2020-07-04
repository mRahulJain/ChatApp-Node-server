package com.android.beastchat.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.android.beastchat.Fragments.InboxFragment
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import io.socket.client.IO
import java.net.URISyntaxException

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

        supportActionBar!!.title = sharedPreferences.getString(constants().USER_NAME, "")!! + "'s Inbox"
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_main_createNewMessage -> {
                val intent = Intent(application, FriendsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return true
            }
        }
        return true
    }
}