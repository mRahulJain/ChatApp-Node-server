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

class InboxFragment : BaseFragments() {
    fun newInstant() : InboxFragment {
        return InboxFragment()
    }

    private lateinit var mUnbinder: Unbinder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_inbox, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewI)
        nav.id = R.id.tab_inbox
        setUpBottomBar(nav, 1)
        nav.selectedItemId = R.id.tab_inbox
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }

}