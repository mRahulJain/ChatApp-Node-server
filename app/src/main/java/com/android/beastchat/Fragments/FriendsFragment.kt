package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.R
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendsFragment : BaseFragments() {
    fun newInstant() : FriendsFragment {
        return FriendsFragment()
    }

    private lateinit var mUnbinder: Unbinder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends, container, false)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewF)
        mUnbinder = ButterKnife.bind(this, rootView)
        nav.id = R.id.tab_friends
        setUpBottomBar(nav, 2)
        nav.selectedItemId = R.id.tab_friends
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }
}