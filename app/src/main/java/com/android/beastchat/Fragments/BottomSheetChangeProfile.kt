package com.android.beastchat.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.ProfileActivity
import com.android.beastchat.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.ClassCastException

class BottomSheetChangeProfile() : BottomSheetDialogFragment() {
    interface BottomSheetChangeProfileListener {
        fun onButtonClickedChangeProfile(flag: Int)
    }

    private lateinit var mListener: BottomSheetChangeProfileListener

    constructor(mListener: BottomSheetChangeProfileListener) : this() {
        this.mListener = mListener
    }

    private lateinit var mUnbinder: Unbinder
    @BindView(R.id.fragment_bottom_change_photo_removePhoto)
    lateinit var mRemovePhoto: ImageView
    @BindView(R.id.fragment_bottom_change_photo_camera)
    lateinit var mCamera: ImageView
    @BindView(R.id.fragment_bottom_change_photo_gallery)
    lateinit var mGallery: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_change_photo, container, false)
        mUnbinder = ButterKnife.bind(this, view)

        return view
    }

    @OnClick(R.id.fragment_bottom_change_photo_removePhoto)
    fun setmRemovePhoto() {
        mListener.onButtonClickedChangeProfile(0)
        dismiss()
    }

    @OnClick(R.id.fragment_bottom_change_photo_camera)
    fun setmCamera() {
        mListener.onButtonClickedChangeProfile(1)
        dismiss()
    }

    @OnClick(R.id.fragment_bottom_change_photo_gallery)
    fun setmGallery() {
        mListener.onButtonClickedChangeProfile(2)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

}