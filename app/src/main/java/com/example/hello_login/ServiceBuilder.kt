package com.example.hello_login

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody
import okhttp3.Request
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Companion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


data class Payload(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("id") val id: String?,
    @SerializedName("group_id") val group_id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?
)

data class Data(
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: User?
)

data class Reply(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Data?,
    @SerializedName("messages") val messages: List<String?>
)

interface PayloadService{
    @POST("auth/login")
    fun getReply(@Query("email") email: String, @Query("password") password: String): Call<Reply>
}

object ServiceBuilder {
    private val client by lazy {



        // Create a trust manager that does not validate certificate chains
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return true
                }
            })


        val logging = okhttp3.logging.HttpLoggingInterceptor()
        logging.level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(logging)

        clientBuilder.build()
    }



    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.tolderfonen.skat.dk/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}