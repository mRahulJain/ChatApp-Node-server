package com.android.beastchat.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseFragments : Fragment() {

    protected lateinit var mCompositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
    }

}