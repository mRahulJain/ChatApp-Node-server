package com.android.beastchat.Services

import android.util.Log
import android.util.Patterns
import android.widget.EditText
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList

class LiveAccountServices {
    private lateinit var mLiveAccountServices: LiveAccountServices

    private var USER_ERROR_EMPTY_PASSWORD = 1
    private var USER_ERROR_EMPTY_EMAIL = 2
    private var USER_ERROR_EMPTY_USERNAME = 3
    private var USER_ERROR_SHORT_PASSWORD = 4
    private var USER_ERROR_EMAIL_BAD_FORMAT = 5
    private var SERVER_SUCCESS = 6
    private var SERVER_FALIURE = 7


    fun getInstant() : LiveAccountServices {
        mLiveAccountServices = LiveAccountServices()
        return mLiveAccountServices
    }

    fun sendRegistrationInfo(userName: EditText, userEmail: EditText, userPassword: EditText, socket : Socket) : Disposable{
        var userDetails : ArrayList<String> = arrayListOf()
        userDetails.add(userName.text.toString())
        userDetails.add(userEmail.text.toString())
        userDetails.add(userPassword.text.toString())
        val userDetailsObservable = Observable.just(userDetails)

        lateinit var disposable: Disposable

        userDetailsObservable
            .subscribeOn(Schedulers.io())
            .map { t ->
                val userName = t[0]
                val userEmail = t[1]
                val userPassword = t[2]
                when {
                    userName!!.isEmpty() -> {
                        USER_ERROR_EMPTY_USERNAME
                    }
                    userEmail!!.isEmpty() -> {
                        USER_ERROR_EMPTY_EMAIL
                    }
                    userPassword!!.isEmpty() -> {
                        USER_ERROR_EMPTY_PASSWORD
                    }
                    userPassword!!.length < 6 -> {
                        USER_ERROR_SHORT_PASSWORD
                    }
                    !isEmailValid(userEmail!!) -> {
                        USER_ERROR_EMAIL_BAD_FORMAT
                    }
                    else -> {
                        var sendData = JSONObject()
                        try {
                            sendData.put("email", "${userEmail!!}")
                            sendData.put("username", "${userName!!}")
                            sendData.put("password", "${userPassword!!}")
                            socket.emit("userData", sendData)
                            SERVER_SUCCESS
                        } catch (e : JSONException) {
                            Log.e("myError", "${e.localizedMessage}")
                            SERVER_FALIURE
                        }
                        0
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        disposable = d
                    }
                }

                override fun onNext(t: Int?) {
                    when (t) {
                        USER_ERROR_EMPTY_EMAIL -> {
                            userEmail.error = "Email address can't be empty"
                        }
                        USER_ERROR_EMPTY_USERNAME -> {
                            userName.error = "UserName can't be empty"
                        }
                        USER_ERROR_EMPTY_PASSWORD -> {
                            userPassword.error = "Password can't be empty"
                        }
                        USER_ERROR_SHORT_PASSWORD -> {
                            userPassword.error = "Password is too short"
                        }
                        USER_ERROR_EMAIL_BAD_FORMAT -> {
                            userEmail.error = "Email is wrongly formatted"
                        }
                    }
                }

                override fun onError(e: Throwable?) {
                }
            })
        return disposable
    }

    private fun isEmailValid(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}