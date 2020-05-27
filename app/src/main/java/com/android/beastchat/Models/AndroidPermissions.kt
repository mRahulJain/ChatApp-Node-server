package com.android.beastchat.Models

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.beastchat.Activities.BaseFragmentActivity

class AndroidPermissions {

    private val EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE = 10
    private val EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE = 11
    private val CAMERA_PERMISSION_REQUEST_CODE = 12
    private lateinit var mActivity: BaseFragmentActivity

    constructor(mActivity: BaseFragmentActivity) {
        this.mActivity = mActivity
    }

    fun checkPermissionForReadExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            mActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if(result ==  PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }
    fun checkPermissionForWriteExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            mActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if(result ==  PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }
    fun checkPermissionForCamera(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            mActivity,
            Manifest.permission.CAMERA
        )
        if(result ==  PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    fun requestPermissionForReadExternalStorage() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )) {
            Toast.makeText(mActivity, "External storage permission is needed", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE
            )
        }
    }
    fun requestPermissionForWriteExternalStorage() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
            Toast.makeText(mActivity, "External storage permission is needed", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
            )
        }
    }
    fun requestPermissionForCamera() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.CAMERA
            )) {
            Toast.makeText(mActivity, "Camera permission is needed", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
}