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

class ProfileFragment : BaseFragments() {
    fun newInstant() : ProfileFragment {
        return ProfileFragment()
    }

    private lateinit var mUnbinder: Unbinder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewP)
        mUnbinder = ButterKnife.bind(this, rootView)
        nav.id = R.id.tab_profile
        setUpBottomBar(nav, 3)
        nav.selectedItemId = R.id.tab_profile
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }
}