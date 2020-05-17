package com.android.beastchat.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_fragment_base.view.*

abstract class BaseFragmentActivity : AppCompatActivity() {
    abstract fun createFragment() : Fragment

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        mAuth = FirebaseAuth.getInstance()
        var mSharedPreferences = getSharedPreferences(
            constants().USER_INFO_PREFERENCE,
            Context.MODE_PRIVATE
        )
        val userEmail = mSharedPreferences.getString("USER_EMAIL", "")

        if(!(this is LoginActivity || this is RegisterActivity)) {
            mListener = FirebaseAuth.AuthStateListener {
                val user = it.currentUser
                if(user == null) {
                    Log.d("myMESSAGE", "user is null")
                    val intent = Intent(application, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if(userEmail.equals("")) {
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
            }
        }

        var fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentById(R.id.activity_fragment_base_fragmentContainer)
        if(fragment == null) {
            fragment = createFragment()
            fragmentManager.beginTransaction()
                .add(R.id.activity_fragment_base_fragmentContainer, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if(!(this is LoginActivity || this is RegisterActivity)) {
            mAuth.addAuthStateListener(mListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!(this is LoginActivity || this is RegisterActivity)) {
            mAuth.removeAuthStateListener(mListener)
        }
    }
}