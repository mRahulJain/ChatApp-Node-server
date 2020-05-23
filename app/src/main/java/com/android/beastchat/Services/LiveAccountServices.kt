package com.android.beastchat.Services

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.android.beastchat.Activities.BaseFragmentActivity
import com.android.beastchat.Activities.InboxActivity
import com.android.beastchat.Models.constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
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
    private var USER_NO_ERRORS = 8


    fun getInstant() : LiveAccountServices {
        mLiveAccountServices = LiveAccountServices()
        return mLiveAccountServices
    }

    fun sendLoginInfo(userEmail: EditText, userPassword: EditText, socket: Socket, activity : BaseFragmentActivity?) : Disposable {
        var userDetails : ArrayList<String> = arrayListOf()
        userDetails.add(userEmail.text.toString())
        userDetails.add(userPassword.text.toString())
        val userDetailsObservable = Observable.just(userDetails)
        lateinit var disposable: Disposable

        userDetailsObservable
            .subscribeOn(Schedulers.io())
            .map { it ->
                val userEmail = it[0]
                val userPassword = it[1]
                when {
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
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            userEmail,
                            userPassword
                        ).addOnCompleteListener {
                            if(it.isSuccessful) {
                                val sendData = JSONObject()
                                sendData.put("email", userEmail)
                                socket.emit("userInfo", sendData)
                            } else {
                                Toast.makeText(
                                    activity,
                                    it.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId()
                            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                                if(!it.isSuccessful) {
                                    Log.e("myError", "An error has occurred")
                                    return@addOnCompleteListener
                                }
                                it.result!!.token
                            }
                        } catch (e: IOException) {
                            Log.e("myError", "${e.localizedMessage}")
                        }
                        FirebaseAuth.getInstance().signOut()
                        USER_NO_ERRORS
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

    fun getAuthToken(data: JSONObject, activity: BaseFragmentActivity?, sharedPreference : SharedPreferences?) : Disposable {
        var jsonObservable = Observable.just(data)
        lateinit var disposable: Disposable
        jsonObservable
            .subscribeOn(Schedulers.io())
            .map {
                var userDetails = arrayListOf<String>()
                try {
                    userDetails.add(it.get("authToken") as String)
                    userDetails.add(it.get("email") as String)
                    userDetails.add(it.get("photo") as String)
                    userDetails.add(it.get("displayName") as String)
                    userDetails
                } catch (e: JSONException) {
                    Log.e("myERROR", e.localizedMessage)
                    userDetails
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<String>>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        disposable = d
                    }
                }

                override fun onNext(t: ArrayList<String>) {
                    val token = t[0]
                    val email = t[1]
                    val photo = t[2]
                    val userName = t[3]

                    if(!email.equals("error")) {
                        FirebaseAuth.getInstance().signInWithCustomToken(token)
                            .addOnCompleteListener {
                                if(it.isSuccessful) {
                                    sharedPreference!!.edit().putString(
                                        constants().USER_EMAIL, email
                                    ).apply()
                                    sharedPreference!!.edit().putString(
                                        constants().USER_NAME, userName
                                    ).apply()
                                    sharedPreference!!.edit().putString(
                                        constants().USER_PICTURE, photo
                                    ).apply()
                                    val intent = Intent(activity, InboxActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    activity!!.startActivity(intent)
                                    activity!!.finish()
                                    Toast.makeText(
                                        activity,
                                        "SIGN IN SUCCESSFUL",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        it.exception!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }

                override fun onError(e: Throwable?) {
                }
            })
        return disposable
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

    fun registerResponse(data: JSONObject, activity : BaseFragmentActivity?) : Disposable {
        val jsonObjectObservable = Observable.just(data)
        lateinit var disposable: Disposable
        jsonObjectObservable
            .subscribeOn(Schedulers.io())
            .map{
                var message = ""
                try {
                    Log.d("myCHECK", "${it.get("text")}")
                    message = it.get("text") as String
                } catch (e: JSONException) {
                    Log.e("myError", "${e.localizedMessage}")
                }
                message
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable?) {
                    if (d != null) {
                        disposable = d
                    }
                }

                override fun onNext(stringResponse: String?) {
                    if(stringResponse.equals("Success")) {
                        activity!!.finish()
                        Toast.makeText(
                            activity,
                            "User registered successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            stringResponse,
                            Toast.LENGTH_LONG
                        ).show()
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