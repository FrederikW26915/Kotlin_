package com.example.hello_login

import android.graphics.PostProcessor
import android.util.Log
import com.google.gson.Gson
import com.google.gson.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

import android.support.*
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

import okhttp3.RequestBody.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


typealias NetworkCallback = (String) -> Unit

object Network {


    private val client = OkHttpClient()


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

        //val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8", json)

        val request = Request.Builder()
            .url("https://api.tolderfonen.skat.dk/api/v1/auth/login")
            //.method("POST")
            .post(requestBody)
            .build()


        Log.e("hej", "lige foer new Call")

        client.newCall(request).enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException){
                Log.e("hej", "onFail")

                e.printStackTrace()



                onError("Failed to connect")


                Log.e("hej", "onFail 2")
            }

            override fun onResponse(call: Call, response: Response) {

                Log.e("hej", "onResponse no success")
                if (response.isSuccessful) {

                    val gson = GsonBuilder().create()

                    val reply = gson.fromJson(response.body?.toString(), Reply::class.java)

                    Log.e("hej", "onResponse")

                    //val reply = response.body()
                    reply?.data?.token?.let { token -> onComplete(token) }
                    reply?.let { Log.e("reply: ", it.toString()) }

                } else {
                    onError("Wrong login or password")
                }
            }
        })
    }
}


/***


    fun login_old(email : String, password : String, onComplete: NetworkCallback, onError: NetworkCallback){

        val request = ServiceBuilder.buildService(PayloadService::class.java)
        val call = request.getReply(email,password)


        Log.e("hej","For")

        call.enqueue(object : Callback<Reply> {

            override fun onResponse(call: Call<Reply>, response: Response<Reply>) {
                Log.e("hej","onResponse no success")
                if (response.isSuccessful) {

                    Log.e("hej","onResponse")

                    val reply = response.body()
                    reply?.data?.token?.let { token -> onComplete(token) }
                    reply?.let {Log.e("reply: " , it.toString()) }

                } else {
                    onError("Wrong login or password")
                }
            }

            override fun onFailure(call: Call<Reply>, t: Throwable) {

                Log.e("hej","onFail")

                t.printStackTrace()

                onError("Failed to connect")

            }
        })
    }
}

*/
