package com.example.hello_login

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.Query
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
    @SerializedName("id") val id: String? = null,
    @SerializedName("group_id") val group_id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("updated_at") val updated_at: String? = null
)

data class Data(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: User? = null
)

data class DataSearch(
    @SerializedName("text") val token: String? = null
)

data class ReplySearch(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: DataSearch? = null,
    @SerializedName("messages") val messages: List<String?>? = null
)

data class Reply(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: Data? = null,
    @SerializedName("messages") val messages: List<String?>? = null
)

interface PayloadService{
    @POST("auth/login")
    fun getReply(@Query("email") email: String, @Query("password") password: String): Call<Reply>
}



data class Phone(
    var id : String,
    var name : String,
    var number: String
)
data class Email(
    var id: String,
    var email: String
)
data class Contact(
    var id : String,
    var groupId : String,
    var name : String,
    var organization: String,
    var department: String,
    var tags : String,
    var notes : String,
    var emails : List<Email>,
    var phones : List<Phone>
)

object ServiceBuilder {
    val client by lazy {

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
        clientBuilder.addInterceptor(TokenInterceptor())

        clientBuilder.build()
    }
}