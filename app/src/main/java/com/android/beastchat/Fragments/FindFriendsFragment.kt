package com.android.beastchat.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Entities.User
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.android.beastchat.Views.FindFriendsViews.FindFriendsAdapter
import com.google.firebase.database.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

class FindFriendsFragment : BaseFragments(), FindFriendsAdapter.UserListener {
    @BindView(R.id.fragment_find_friends_searchBar)
    lateinit var mSearchBar : EditText

    @BindView(R.id.fragment_find_friends_recyclerView)
    lateinit var mRecyclerView : RecyclerView

    @BindView(R.id.fragment_find_friends_noResults)
    lateinit var mNoResults : TextView

    private lateinit var mUnbinder: Unbinder
    private lateinit var mGetAllUserReference : DatabaseReference
    private lateinit var mGetAllUserListener : ValueEventListener
    private var mUserEmailString : String? = null
    private lateinit var mAllUsers : ArrayList<User>
    private lateinit var mAdapter : FindFriendsAdapter
    private lateinit var mSearchBarString : PublishSubject<String>

    private lateinit var mGetAllFriendRequestsSentReference : DatabaseReference
    private lateinit var mGetAllFriendRequestsSentListener : ValueEventListener
    private lateinit var mLiveFriendsServices: LiveFriendsServices

    private lateinit var mGetAllFriendRequestsReceivedReference : DatabaseReference
    private lateinit var mGetAllFriendRequestsReceivedListener : ValueEventListener

    private lateinit var mGetAllCurrentUserFriendsReference : DatabaseReference
    private lateinit var mGetAllCurrentUserFriendsListener : ValueEventListener

    lateinit var mFriendRequestSentMap : HashMap<String, User>
    private lateinit var mSocket: Socket

    fun newInstant() : FindFriendsFragment {
        return FindFriendsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()

        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        mFriendRequestSentMap = HashMap()
    }

    fun setmFriendRequestSentMap(friendRequestSentMap : HashMap<String, User>) {
        mFriendRequestSentMap.clear()
        this.mFriendRequestSentMap.putAll(friendRequestSentMap)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_find_friends, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        mAllUsers = arrayListOf()
        mAdapter = FindFriendsAdapter(activity as BaseFragmentActivity, this)
        mGetAllUserListener = getAllUsers(mAdapter, mUserEmailString)
        mGetAllUserReference = FirebaseDatabase.getInstance().getReference().child(constants().FIREBASE_USERS_PATH)
        mGetAllUserReference.addValueEventListener(mGetAllUserListener)
        mRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        mRecyclerView.adapter = mAdapter


        mGetAllFriendRequestsSentReference = FirebaseDatabase.getInstance().getReference()
            .child(constants().FIREBASE_FRIEND_REQUEST_SENT_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mGetAllFriendRequestsSentListener = mLiveFriendsServices.getFriendRequestsSent(mAdapter, this)
        mGetAllFriendRequestsSentReference.addValueEventListener(mGetAllFriendRequestsSentListener)

        mGetAllFriendRequestsReceivedReference = FirebaseDatabase.getInstance().getReference()
            .child(constants().FIREBASE_FRIEND_REQUEST_RECEIVED_PATH)
            .child(constants().encodeEmail(mUserEmailString))
        mGetAllFriendRequestsReceivedListener = mLiveFriendsServices.getFriendRequestsReceived(mAdapter)
        mGetAllFriendRequestsReceivedReference.addValueEventListener(mGetAllFriendRequestsReceivedListener)

        mCompositeDisposable.add(createSearchBarDisposable())
        mListenToSearchBar()

        mGetAllCurrentUserFriendsListener = mLiveFriendsServices.getAllCurrentUsersFriendsMap(mAdapter)
        mGetAllCurrentUserFriendsReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_FRIENDS)
            .child(constants().encodeEmail(mUserEmailString))
        mGetAllCurrentUserFriendsReference.addValueEventListener(mGetAllCurrentUserFriendsListener)

        return rootView
    }

    fun createSearchBarDisposable(): Disposable {
        mSearchBarString = PublishSubject.create()
        lateinit var mDisposable: Disposable
        mSearchBarString
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .map {
                mLiveFriendsServices.getMatchingUsers(mAllUsers, it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<User>>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        mDisposable = d
                    }
                }

                override fun onNext(t: ArrayList<User>?) {
                    if(t!!.size == 0 ) {
                        mNoResults.isVisible = true
                        mRecyclerView.isVisible = false
                    } else {
                        mNoResults.isVisible = false
                        mRecyclerView.isVisible = true
                    }
                    mAdapter.setmUsers(t)
                }

                override fun onError(e: Throwable?) {
                }

            })
        return mDisposable
    }

    private fun mListenToSearchBar() {
        mSearchBar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mSearchBarString.onNext(p0.toString())
            }

        })
    }

    fun getAllUsers(adapter: FindFriendsAdapter, currentUserEmail: String?) : ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity!!, "Can't load users", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                mAllUsers.clear()
                for(data in p0.children) {
                    val mUser = data.getValue(User::class.java)
                    if(mUser!!.email != mUserEmailString) {
                        mAllUsers.add(mUser)
                    }
                    adapter.setmUsers(mAllUsers)
                }
            }

        }
        return listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
        if(mGetAllUserListener != null) {
            mGetAllUserReference.removeEventListener(mGetAllUserListener)
        }
        if(mGetAllFriendRequestsSentListener != null) {
            mGetAllFriendRequestsSentReference.removeEventListener(mGetAllFriendRequestsSentListener)
        }
        if(mGetAllFriendRequestsReceivedListener != null) {
            mGetAllFriendRequestsReceivedReference.removeEventListener(mGetAllFriendRequestsReceivedListener)
        }
        if(mGetAllCurrentUserFriendsListener != null) {
            mGetAllCurrentUserFriendsReference.removeEventListener(mGetAllCurrentUserFriendsListener)
        }
    }

    override fun onUserClicked(user: User) {
        if(constants().isIncludedInMap(mFriendRequestSentMap, user)) {
            mCompositeDisposable.add(mLiveFriendsServices
                .addOrRemoveFriendRequest(mSocket, mUserEmailString,user!!.email, "1"))
            mGetAllFriendRequestsSentReference.child(constants().encodeEmail(user!!.email)).removeValue()
        } else {
            mGetAllFriendRequestsSentReference.child(constants().encodeEmail(user!!.email)).setValue(user)
            mCompositeDisposable.add(mLiveFriendsServices
                .addOrRemoveFriendRequest(mSocket, mUserEmailString,user!!.email, "0"))
        }
    }
}