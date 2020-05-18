package com.android.beastchat.Views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.android.beastchat.Fragments.FindFriendsFragment
import com.android.beastchat.Fragments.FriendRequestsFragment
import com.android.beastchat.Fragments.UserFriendsFragment

class FriendsViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        lateinit var returnFragment : Fragment

        when (position) {
            0 -> {
                returnFragment = UserFriendsFragment().newInstant()
            }
            1 -> {
                returnFragment = FriendRequestsFragment().newInstant()
            }
            2 -> {
                returnFragment = FindFriendsFragment().newInstant()
            }
        }
        return returnFragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var ch : CharSequence? = null
        when (position) {
            0 -> {
                ch = "Friends"
            }
            1 -> {
                ch = "Requests"
            }
            2 -> {
                ch = "Find Friends"
            }
        }
        return ch
    }

}