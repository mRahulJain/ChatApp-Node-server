package com.android.beastchat.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.beastchat.R
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_activity.*

class ImageActivity : AppCompatActivity() {

    private lateinit var mSlidr: SlidrInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)

        supportActionBar!!.hide()

        mSlidr = Slidr.attach(this)

        val imageUri = intent.getStringExtra("imageUri")

        Picasso.with(this)
            .load(imageUri)
            .into(image_activity_image)
    }
}