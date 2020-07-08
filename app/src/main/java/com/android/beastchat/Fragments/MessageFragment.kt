package com.android.beastchat.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.OtherProfileActivity
import com.android.beastchat.Entities.ChatRoom
import com.android.beastchat.Entities.Message
import com.android.beastchat.Models.constants
import com.android.beastchat.R
import com.android.beastchat.Services.LiveFriendsServices
import com.android.beastchat.Views.MessageViews.MessagesAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

class MessageFragment : BaseFragments() {
    val FRIEND_DETAILS_EXTRA = "FRIEND_DETAILS_EXTRA"

    lateinit var mFriendEmailString: String
    lateinit var mFriendPictureString: String
    lateinit var mFriendNameString: String
    lateinit var mUserEmailString: String

    @BindView(R.id.fragment_messages_back)
    lateinit var mBackButton: LinearLayout
    @BindView(R.id.fragment_messages_friendPictureToolbar)
    lateinit var mPictureToolbar: RoundedImageView
    @BindView(R.id.fragment_messages_friendNameToolbar)
    lateinit var mNameToolbar: TextView
    @BindView(R.id.fragment_messages_friendPicture)
    lateinit var mFriendPicture: RoundedImageView
    @BindView(R.id.fragment_messages_friendName)
    lateinit var mFriendName: TextView
    @BindView(R.id.fragment_messages_messageBox)
    lateinit var mMessageBox: EditText
    @BindView(R.id.fragment_messages_sendMessage)
    lateinit var mSendMessage: TextView
    @BindView(R.id.fragment_messages_recyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.fragment_messages_onlineStatus)
    lateinit var mOnlineStatus: ImageView

    lateinit var mUnbinder: Unbinder

    private lateinit var mGetAllMessagesReference: DatabaseReference
    private lateinit var mGetAllMessagesListener: ValueEventListener
    private lateinit var mSocket: Socket
    private lateinit var mLiveFriendsServices: LiveFriendsServices

    private lateinit var mMessageSubject: PublishSubject<String>
    private lateinit var mUserChatRoomReference: DatabaseReference

    fun newInstant(friendDetails: ArrayList<String>): MessageFragment {
        val arguments = Bundle()
        arguments.putStringArrayList(FRIEND_DETAILS_EXTRA, friendDetails)
        val messageFragment = MessageFragment()
        messageFragment.arguments = arguments
        return messageFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLiveFriendsServices = LiveFriendsServices().getInstant()
        try {
            mSocket = IO.socket(constants().IP_LOCALHOST)
        } catch (e: URISyntaxException) {
            Log.d("myError", "${e.localizedMessage}")
        }
        mSocket.connect()

        val friendDetails = arguments!!.getStringArrayList(FRIEND_DETAILS_EXTRA)
        mFriendEmailString = friendDetails!![0]
        mFriendPictureString = friendDetails!![1]
        mFriendNameString = friendDetails!![2]
        mUserEmailString = mSharedPreferences.getString(constants().USER_EMAIL, "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_messages, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        if(mFriendPictureString != constants().DEFAULT_USER_PICTURE) {
            Picasso.with(activity!!)
                .load(mFriendPictureString)
                .into(mFriendPicture)
        } else {
            mFriendPicture.setImageResource(R.drawable.user_image)
        }
        mFriendName.text = mFriendNameString

        //toolbar
        if(mFriendPictureString != constants().DEFAULT_USER_PICTURE) {
            Picasso.with(context)
                .load(mFriendPictureString)
                .into(mPictureToolbar)
        } else {
            mPictureToolbar.setImageResource(R.drawable.user_image)
        }
        mNameToolbar.text = mFriendNameString

        val adapter = MessagesAdapter(activity!! as BaseFragmentActivity, mUserEmailString, mFriendEmailString)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mGetAllMessagesReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_MESSAGES)
            .child(constants().encodeEmail(mUserEmailString))
            .child(constants().encodeEmail(mFriendEmailString))
        mGetAllMessagesListener = mLiveFriendsServices.getAllMessages(mRecyclerView, mFriendName, mFriendPicture, adapter, mUserEmailString, mFriendEmailString)
        mGetAllMessagesReference.addValueEventListener(mGetAllMessagesListener)
        mRecyclerView.adapter = adapter

        mUserChatRoomReference = FirebaseDatabase.getInstance()
            .getReference().child(constants().FIREBASE_PATH_USER_CHATROOM)
            .child(constants().encodeEmail(mUserEmailString))
        mCompositeDisposable.add(
            createChatRoomDisposable()
        )
        messageBoxListener()
        recyclerViewAutoScrollListener()

        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
        mLiveFriendsServices.checkUserOnline(mOnlineStatus, mFriendEmailString)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mLiveFriendsServices.putUserOnline(mSocket, mUserEmailString)
    }

    private fun recyclerViewAutoScrollListener() {
        if (Build.VERSION.SDK_INT >= 11) {
            mRecyclerView.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(Runnable {
                        mRecyclerView.smoothScrollToPosition(
                            mRecyclerView.adapter!!.getItemCount() - 1
                        )
                    }, 100)
                }
            })
        }
    }

    @OnClick(R.id.fragment_messages_back)
    fun back() {
        activity!!.finish()
    }

    @OnClick(R.id.fragment_messages_friendNameToolbar)
    fun setmViewFriendProfileThroughToolbar() {
        setProfile()
    }
    @OnClick(R.id.fragment_messages_friendPicture)
    fun setmViewFriendProfileThroughImage() {
        setProfile()
    }
    @OnClick(R.id.fragment_messages_friendName)
    fun setmViewFriendProfileThroughName() {
        setProfile()
    }

    private fun setProfile() {
        val intent = Intent(context, OtherProfileActivity::class.java)
        intent.putExtra("email", mFriendEmailString)
        intent.putExtra("userType", constants().USER_TYPE_FRIEND_MESSAGES)
        startActivity(intent)
    }

    @OnClick(R.id.fragment_messages_sendMessage)
    fun mSendMessage() {
        if(mMessageBox.text.toString().trim() == "") {
            Toast.makeText(activity!!, "Enter something", Toast.LENGTH_SHORT).show()
        } else {
            var chatRoom = ChatRoom(
                mFriendPictureString,
                mFriendNameString,
                mFriendEmailString,
                mMessageBox.text.toString().trim(),
                mUserEmailString,
                lastMessageRead = true,
                sentLastMessage = true
            )
            mUserChatRoomReference
                .child(constants().encodeEmail(mFriendEmailString))
                .setValue(chatRoom)

            val newMessageReference = mGetAllMessagesReference.push()
            val message = Message(
                messageId = newMessageReference.key!!,
                messageText = mMessageBox.text.toString().trim(),
                messageSenderEmail = mUserEmailString,
                messageSenderPicture = mSharedPreferences.getString(constants().USER_PICTURE, "")!!
            )
            newMessageReference.setValue(message)
            mCompositeDisposable.add(
                mLiveFriendsServices.sendMessage(
                    mSocket,
                    newMessageReference.key!!,
                    mUserEmailString,
                    mSharedPreferences.getString(constants().USER_PICTURE, "")!!,
                    mMessageBox!!.text.toString().trim(),
                    mFriendEmailString,
                    mSharedPreferences!!.getString(constants().USER_NAME, "")!!
                )
            )
            mMessageBox.setText("")
        }
    }

    fun createChatRoomDisposable(): Disposable {
        mMessageSubject = PublishSubject.create()
        lateinit var mDisposable: Disposable
        mMessageSubject.debounce(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        mDisposable = d
                    }
                }

                override fun onNext(t: String?) {
                    if(t!!.trim().isNotEmpty()) {
                        var chatRoom = ChatRoom(
                            mFriendPictureString,
                            mFriendNameString,
                            mFriendEmailString,
                            t!!,
                            mUserEmailString,
                            lastMessageRead = true,
                            sentLastMessage = false
                        )
                        mUserChatRoomReference
                            .child(constants().encodeEmail(mFriendEmailString))
                            .setValue(chatRoom)
                    }
                }
                override fun onError(e: Throwable?) {
                    TODO("Not yet implemented")
                }
            })
        return mDisposable
    }

    private fun messageBoxListener() {
        mMessageBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mMessageSubject.onNext(p0.toString())
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()

        if(mGetAllMessagesListener != null) {
            mGetAllMessagesReference.removeEventListener(mGetAllMessagesListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}