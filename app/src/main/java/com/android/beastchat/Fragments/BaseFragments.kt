package com.android.beastchat.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.beastchat.Activities.FriendsActivity
import com.android.beastchat.Activities.InboxActivity
import com.android.beastchat.Activities.ProfileActivity
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.socket.client.IO
import java.net.URISyntaxException

open class BaseFragments : Fragment() {
    protected lateinit var mCompositeDisposable: CompositeDisposable
    protected lateinit var mSharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCompositeDisposable = CompositeDisposable()
        mSharedPreferences = activity!!.getSharedPreferences(
            constants().USER_INFO_PREFERENCE,
            Context.MODE_PRIVATE
        )
    }

    fun setUpBottomBar(bottomAppBar: BottomNavigationView, index : Int) {
        bottomAppBar.setOnNavigationItemSelectedListener {
            when (index) {
                1 -> {
                    if(it.itemId == R.id.tab_friends) {
                        val intent = Intent(activity, FriendsActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out)
                    } else if(it.itemId == R.id.tab_profile) {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    }
                    true
                }
                2 -> {
                    if(it.itemId == R.id.tab_inbox) {
                        val intent = Intent(activity, InboxActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    } else if(it.itemId == R.id.tab_profile) {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    }
                    true
                }
                3 -> {
                    if(it.itemId == R.id.tab_friends) {
                        val intent = Intent(activity, FriendsActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    } else if(it.itemId == R.id.tab_inbox) {
                        val intent = Intent(activity, InboxActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        activity!!.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    }
                    true
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
    }

}