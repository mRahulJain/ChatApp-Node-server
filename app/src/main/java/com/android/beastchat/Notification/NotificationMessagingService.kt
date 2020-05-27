package com.android.beastchat.Notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.beastchat.Activities.FriendsActivity
import com.android.beastchat.Activities.InboxActivity
import com.android.beastchat.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        var title = p0.data!!["title"]
        var body = p0.data!!["body"]
        sendNotification(title!!, body!!)
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String, body: String) {
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
        lateinit var clickableNotification: NotificationCompat.Builder
        if(title == "New Message") {
            clickableNotification =  NotificationCompat.Builder(this, "first")
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(pattern.toLongArray())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntentFriendRequest)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
        }

        nm.notify(0, clickableNotification.build())
    }
}