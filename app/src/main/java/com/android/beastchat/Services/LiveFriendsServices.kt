package com.android.beastchat.Services

import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.beastchat.Entities.User
import com.android.beastchat.Fragments.FindFriendsFragment
import com.android.beastchat.Views.FindFriendsViews.FindFriendsAdapter
import com.android.beastchat.Views.FriendRequestViews.FriendRequestsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LiveFriendsServices {
    private lateinit var mLiveFriendsServices: LiveFriendsServices

    private var SERVER_SUCCESS = 6
    private var SERVER_FALIURE = 7

    fun getInstant(): LiveFriendsServices{
        mLiveFriendsServices = LiveFriendsServices()
        return mLiveFriendsServices
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