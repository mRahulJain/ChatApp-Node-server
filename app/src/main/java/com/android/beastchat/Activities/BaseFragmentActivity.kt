package com.android.beastchat.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.beastchat.R
import kotlinx.android.synthetic.main.activity_fragment_base.view.*

abstract class BaseFragmentActivity : AppCompatActivity() {
    abstract fun createFragment() : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        var fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentById(R.id.activity_fragment_base_fragmentContainer)
        if(fragment == null) {
            fragment = createFragment()
            fragmentManager.beginTransaction()
                .add(R.id.activity_fragment_base_fragmentContainer, fragment)
                .commit()
        }
    }
}