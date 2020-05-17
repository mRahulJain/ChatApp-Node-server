package com.android.beastchat.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.beastchat.Models.constants
import io.reactivex.rxjava3.disposables.CompositeDisposable

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

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
    }

}