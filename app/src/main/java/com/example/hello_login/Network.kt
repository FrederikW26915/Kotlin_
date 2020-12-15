package com.example.hello_login

import android.util.Log
import com.example.hello_login.Network.token
import com.google.gson.Gson
import com.google.gson.*
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception



class TokenInterceptor() : okhttp3.Interceptor {
    @Throws(java.io.IOException::class)
    override fun intercept(chain: okhttp3.Interceptor.Chain):
            okhttp3.Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
        newRequest.header("Authorization", "Bearer " + token)
        return chain.proceed(newRequest.build())
    }
}

typealias NetworkCallback = (String) -> Unit

object Network {

    var token = "";

    private val client = ServiceBuilder.client

    fun login(
        email: String,
        password: String,
        onComplete: NetworkCallback,
        onError: NetworkCallback
    ) {

        val payload = Payload(email, password)

        val json = Gson().toJson(payload)

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.tolderfonen.skat.dk/api/v1/auth/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException){
                onError("Failed to connect")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val rJson = response.body?.string()
                        val reply = Gson().fromJson<Reply>(rJson, Reply::class.java)

                        if (reply.success == true){
                            Log.e("hej", "true")
                            token = reply.data?.token.toString()
                            onComplete(reply.toString())
                            Log.e("hej", token)
                            Log.e("hej", "true2")
                        } else {
                            onError("Wrong login or password")
                        }

                    }
                    catch(e: Exception){
                        e.printStackTrace()
                    }

                } else {
                    onError("Wrong login or password")
                }
            }
        })
    }

    fun search(
        text: String
        /*,
        onComplete: NetworkCallback,
        onError: NetworkCallback*/
    ) {

        //val payload = text

        val json = Gson().toJson(text)

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.tolderfonen.skat.dk/api/v1/search")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException){
                //onError("Failed to connect")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("hej",response.isSuccessful.toString() + "response")
                    try {
                        val rJson = response.body?.string()
                        Log.e("hej", rJson)
                        val reply = Gson().fromJson<ReplySearch>(rJson, ReplySearch::class.java)

                        if (reply.success == true){
                            Log.e("hej", "true")
                            //onComplete(reply.toString())
                        } else {
                            Log.e("hej", "false")
                            //onError("Wrong login or password")
                        }

                    }
                    catch(e: Exception){
                        e.printStackTrace()
                        Log.e("hej", "wrong responce")
                    }

                } else {
                    //onError("Wrong login or password")
                }
            }
        })
    }
}
