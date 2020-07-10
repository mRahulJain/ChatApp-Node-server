package com.android.beastchat.Notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.beastchat.Activities.FriendsActivity
import com.android.beastchat.Activities.InboxActivity
import com.android.beastchat.Models.EncryptDecryptHelper
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class NotificationMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        var title = p0.data!!["title"]
        var senderName = p0.data!!["senderName"]
        var message = EncryptDecryptHelper().decryptWithAES(
            p0.data!!["body"],
            constants().AES_ENCRYPTION_CONSTANT
        )
        var body = "$senderName: $message"
        var picture = p0.data!!["image"]
        sendNotification(title!!, body!!, picture!!)
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String, body: String, picture: String) {
        Log.d("myPIC", "${picture}")
        val intentFriendRequest = Intent(this, FriendsActivity::class.java)
        val intentNewMessage = Intent(this, InboxActivity::class.java)
        intentFriendRequest.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intentNewMessage.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntentFriendRequest = PendingIntent.getActivity(
            this,
            0,
            intentFriendRequest,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentNewMessage= PendingIntent.getActivity(
            this,
            0,
            intentNewMessage,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pattern: Array<Long> = arrayOf(
            500,500,500,500,500
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = nm.getNotificationChannel("first")
            if(mChannel == null) {
                mChannel = NotificationChannel("first", "FriendRequest", importance)
                mChannel!!.description = "FriendRequest"
                mChannel!!.enableVibration(true)
                mChannel!!.lightColor = Color.BLUE
                mChannel!!.vibrationPattern = pattern!!.toLongArray()
                nm.createNotificationChannel(mChannel)
            }
        }

        var myBitMap :Bitmap = drawableToBitmap(resources.getDrawable(R.drawable.user_image))!!
        try {
            val url = URL(picture)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            myBitMap = BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        lateinit var clickableNotification: NotificationCompat.Builder
        if(title == "New Message") {
            clickableNotification =  NotificationCompat.Builder(this, "first")
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(pattern.toLongArray())
                .setSmallIcon(R.drawable.sms_small)
                .setLargeIcon(myBitMap)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntentNewMessage)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
        } else {
            clickableNotification =  NotificationCompat.Builder(this, "first")
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(pattern.toLongArray())
                .setSmallIcon(R.drawable.sms_small)
                .setLargeIcon(myBitMap)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntentFriendRequest)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
        }

        nm.notify(0, clickableNotification.build())
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }
}