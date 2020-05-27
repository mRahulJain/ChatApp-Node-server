package com.android.beastchat.Services

import android.content.Context
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.Entities.Message
import com.android.beastchat.Entities.User
import com.android.beastchat.Fragments.FindFriendsFragment
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Views.ChatRoomViews.ChatRoomAdapter
import com.android.beastchat.Views.FindFriendsViews.FindFriendsAdapter
import com.android.beastchat.Views.FriendRequestViews.FriendRequestsAdapter
import com.android.beastchat.Views.FriendViews.FriendAdapter
import com.android.beastchat.Views.MessageViews.MessagesAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makeramen.roundedimageview.RoundedImageView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.socket.client.Socket
import kotlinx.android.synthetic.main.badge_layout.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LiveFriendsServices {
    private lateinit var mLiveFriendsServices: LiveFriendsServices
    var notificationsBadge : View?  = null
    var mBottomNavigationItemView: BottomNavigationItemView? = null
    private var SERVER_SUCCESS = 6
    private var SERVER_FALIURE = 7

    fun getInstant(): LiveFriendsServices{
        mLiveFriendsServices = LiveFriendsServices()
        return mLiveFriendsServices
    }

    fun sendMessage(
        socket: Socket,
        messageId: String,
        messageSenderEmail: String,
        messageSenderPicture: String,
        message: String,
        friendEmail: String,
        messageSenderName: String
    ): Disposable {
        val details = arrayListOf<String>()
        details.add(messageSenderEmail)
        details.add(messageSenderPicture)
        details.add(message)
        details.add(friendEmail)
        details.add(messageId)
        details.add(messageSenderName)
        lateinit var mDisposable: Disposable
        val messageObservable = Observable.just(details)
        messageObservable.subscribeOn(Schedulers.io())
            .map {
                val sendData = JSONObject()
                try {
                    sendData.put("senderEmail", it[0])
                    sendData.put("senderPicture", it[1])
                    sendData.put("messageText", it[2])
                    sendData.put("friendEmail", it[3])
                    sendData.put("messageId", it[4])
                    sendData.put("senderName", it[5])
                    socket.emit("details", sendData)
                    SERVER_SUCCESS
                } catch (e: JSONException) {
                    Log.e("myError", e.localizedMessage)
                    SERVER_FALIURE
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        mDisposable = d
                    }
                }

                override fun onNext(t: Int?) {
                }

                override fun onError(e: Throwable?) {
                }

            })
        return mDisposable
    }

    fun isSeenMessage(textView: TextView, mCurrentUserEmail: String, mFriendEmailString: String) {
        var mSeenRef = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_CHATROOM)
            .child(constants().encodeEmail(mFriendEmailString))
            .child(constants().encodeEmail(mCurrentUserEmail))
            .child("lastMessageRead")
        mSeenRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    textView.isVisible = p0.value == true
                }
            }
        })
    }

    fun getAllMessages(recyclerView: RecyclerView, textView: TextView, imageView: RoundedImageView, messagesAdapter: MessagesAdapter, userEmail: String) : ValueEventListener{
        val listMessages = arrayListOf<Message>()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                listMessages.clear()
                val newMessagesReference = FirebaseDatabase.getInstance()
                    .getReference().child(constants().FIREBASE_PATH_USER_NEW_MESSAGES)
                    .child(constants().encodeEmail(userEmail))
                for(snap in p0.children) {
                    val message = snap.getValue(Message::class.java)
                    newMessagesReference.child(message!!.messageId).removeValue()
                    listMessages.add(message!!)
                }
                if(listMessages.isEmpty()) {
                    recyclerView.isVisible = false
                    textView.isVisible = true
                    imageView.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    recyclerView.scrollToPosition(listMessages.size - 1)
                    textView.isVisible = false
                    imageView.isVisible = false
                    messagesAdapter.setmMessages(listMessages)
                }
            }

        }

        return listener
    }

    fun getAllChatRooms(recyclerView: RecyclerView, textView: TextView, chatRoomAdapter: ChatRoomAdapter): ValueEventListener {
        val listRooms = arrayListOf<ChatRoom>()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                listRooms.clear()
                for(snap in p0.children) {
                    val chatRoom = snap.getValue(ChatRoom::class.java)
                    listRooms.add(chatRoom!!)
                }

                if(listRooms.isEmpty()) {
                    recyclerView.isVisible = false
                    textView.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    textView.isVisible = false
                    chatRoomAdapter.setmChatRooms(listRooms)
                }
            }
        }
        return listener
    }

    fun addOrRemoveFriendRequest(socket: Socket, userEmail: String?, friendsEmail: String, requestCode: String): Disposable {
        val details = arrayListOf<String>()
        details.add(friendsEmail)
        details.add(userEmail!!)
        details.add(requestCode)
        val listObservable = Observable.just(details)
        lateinit var mDisposable: Disposable

        listObservable
            .subscribeOn(Schedulers.io())
            .map {
                val sendData = JSONObject()
                try {
                    sendData.put("email", it[0])
                    sendData.put("userEmail", it[1])
                    sendData.put("requestCode", it[2])
                    socket.emit("friendRequest", sendData)
                    SERVER_SUCCESS
                } catch (e: JSONException) {
                    Log.d("myERROR", e.localizedMessage)
                    SERVER_FALIURE
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        mDisposable = d
                    }
                }

                override fun onNext(t: Int?) {
                }

                override fun onError(e: Throwable?) {
                }

            })
        return mDisposable
    }

    fun approveDeclineFriendRequest(socket: Socket, userEmail: String?, friendsEmail: String, requestCode: String): Disposable {
        val details = arrayListOf<String>()
        details.add(friendsEmail)
        details.add(userEmail!!)
        details.add(requestCode)
        val listObservable = Observable.just(details)
        lateinit var mDisposable: Disposable

        listObservable
            .subscribeOn(Schedulers.io())
            .map {
                val sendData = JSONObject()
                try {
                    sendData.put("friendEmail", it[0])
                    sendData.put("userEmail", it[1])
                    sendData.put("requestCode", it[2])
                    socket.emit("friendRequestResponse", sendData)
                    SERVER_SUCCESS
                } catch (e: JSONException) {
                    Log.d("myERROR", e.localizedMessage)
                    SERVER_FALIURE
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        mDisposable = d
                    }
                }

                override fun onNext(t: Int?) {
                }

                override fun onError(e: Throwable?) {
                }

            })
        return mDisposable
    }

    fun getAllFriendRequests(adapter: FriendRequestsAdapter, recyclerView: RecyclerView, textView: TextView): ValueEventListener {
        var mUsers = arrayListOf<User>()
        var listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mUsers.clear()
                for(snap in p0.children) {
                    val user = snap.getValue(User::class.java)
                    mUsers.add(user!!)
                }
                if(mUsers.isEmpty()) {
                    recyclerView.isVisible = false
                    textView.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    textView.isVisible = false
                    adapter.setmUsers(mUsers)
                }
            }
        }
        return listener
    }

    fun getFriendRequestsSent(adapter: FindFriendsAdapter, fragment : FindFriendsFragment) : ValueEventListener {
        var userHashMap : HashMap<String, User> = HashMap()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                userHashMap.clear()
                for(snap in p0.children) {
                    val user = snap.getValue(User::class.java)
                    userHashMap[user!!.email] = user
                }

                adapter.setmFriendRequestSentMap(userHashMap)
                fragment.setmFriendRequestSentMap(userHashMap)
            }

        }
        return listener
    }
    fun getFriendRequestsReceived(adapter: FindFriendsAdapter) : ValueEventListener {
        var userHashMap : HashMap<String, User> = HashMap()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                userHashMap.clear()
                for(snap in p0.children) {
                    val user = snap.getValue(User::class.java)
                    userHashMap[user!!.email] = user
                }

                adapter.setmFriendRequestRecievedMap(userHashMap)
            }

        }
        return listener
    }

    fun getAllCurrentUsersFriendsMap(adapter: FindFriendsAdapter) : ValueEventListener {
        val friendHashMap = HashMap<String, User>()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                friendHashMap.clear()
                for(snap in p0.children) {
                    val friend = snap.getValue(User::class.java)
                    friendHashMap.put(friend!!.email, friend)
                }
                adapter.setmCurrentUserFriends(friendHashMap)
            }

        }
        return listener
    }

    fun getAllFriendsListener(adapter: FriendAdapter, recyclerView: RecyclerView,textView: TextView) : ValueEventListener {
        val mAllFriends = arrayListOf<User>()
        val listener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mAllFriends.clear()
                for(snap in p0.children) {
                    val friend = snap.getValue(User::class.java)
                    mAllFriends.add(friend!!)
                }
                adapter.setmUser(mAllFriends)
                if(mAllFriends.isEmpty()) {
                    recyclerView.isVisible = false
                    textView.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    textView.isVisible = false
                }
            }

        }
        return listener
    }

    fun getFriendRequestBottom(bottomNavigationView: BottomNavigationView, tagId: Int, context: Context): ValueEventListener{
        val users = arrayListOf<User>()
        val listener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                users.clear()
                for(snap in p0.children) {
                    val user = snap.getValue(User::class.java)
                    users.add(user!!)
                }
                if(!users.isEmpty()) {
                    addBadge(
                        bottomNavigationView,
                        tagId,
                        users!!.size.toString(),
                        context
                    )
                } else {
                    removeBadge()
                }
            }

        }
        return listener
    }

    fun getAllNewMessages(bottomNavigationView: BottomNavigationView, tagId: Int, context: Context): ValueEventListener {
        val newMessages = arrayListOf<Message>()
        val listener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                newMessages.clear()
                for(snap in p0.children) {
                    val message = snap.getValue(Message::class.java)
                    newMessages.add(message!!)
                }

                if(newMessages.isNotEmpty()) {
                    addBadge(
                        bottomNavigationView,
                        tagId,
                        newMessages!!.size.toString(),
                        context
                    )
                } else {
                    removeBadge()
                }
            }

        }

        return listener
    }

    private fun getBadge(bottomNavigationView: BottomNavigationView, index: Int, context: Context) : View {
        if (notificationsBadge != null){
            return notificationsBadge!!
        }
        var mBottomNavigationMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        when(index) {
            R.id.tab_friends -> {
                val view = mBottomNavigationMenuView!!.getChildAt(1)
                mBottomNavigationItemView = view as BottomNavigationItemView
                true
            }
            R.id.tab_inbox -> {
                val view = mBottomNavigationMenuView!!.getChildAt(0)
                mBottomNavigationItemView = view as BottomNavigationItemView
                true
            }
        }
        notificationsBadge = LayoutInflater.from(context).inflate(
            R.layout.badge_layout,
            mBottomNavigationItemView,false)
        mBottomNavigationItemView!!.addView(notificationsBadge)
        return notificationsBadge!!
    }

    private fun addBadge(bottomNavigationView: BottomNavigationView, index: Int, count : String, context: Context) {
        notificationsBadge = getBadge(bottomNavigationView, index, context)
        notificationsBadge?.notifications_badge?.text = count
    }

    private fun removeBadge() {
        if(mBottomNavigationItemView != null) {
            mBottomNavigationItemView!!.removeView(notificationsBadge)
        }
    }

    fun getMatchingUsers(mUsers : ArrayList<User>, userEmail: String?) : ArrayList<User> {
        if(userEmail!!.isEmpty()) {
            return mUsers
        }
        val searchedList : ArrayList<User> = arrayListOf()
        for(user in mUsers) {
            if((user!!.email.toLowerCase()).startsWith(userEmail.toLowerCase())) {
                searchedList.add(user)
            }
        }
        return searchedList
    }
}