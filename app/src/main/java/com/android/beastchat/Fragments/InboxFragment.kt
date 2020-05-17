package com.android.beastchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.beastchat.R

class InboxFragment : BaseFragments() {
    fun newInstant() : InboxFragment {
        return InboxFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_inbox, container, false)

        return rootView
    }

}