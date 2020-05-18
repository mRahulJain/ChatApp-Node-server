package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.R
import com.android.beastchat.Views.FriendsViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class FriendsFragment : BaseFragments() {
    fun newInstant() : FriendsFragment {
        return FriendsFragment()
    }

    private lateinit var mUnbinder: Unbinder

    @BindView(R.id.fragment_friends_tabLayout)
    lateinit var mTabLayout : TabLayout

    @BindView(R.id.fragment_friends_viewPager)
    lateinit var mViewPager : ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)

        val nav = rootView!!.findViewById<BottomNavigationView>(R.id.nav_viewF)
        nav.id = R.id.tab_friends
        setUpBottomBar(nav, 2)
        nav.selectedItemId = R.id.tab_friends

        var friendViewPagerAdapter = FriendsViewPagerAdapter(activity!!.supportFragmentManager)
        mViewPager.adapter = friendViewPagerAdapter
        mTabLayout.setupWithViewPager(mViewPager)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }
}