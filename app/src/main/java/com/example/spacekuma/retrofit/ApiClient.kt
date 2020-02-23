package com.example.spacekuma.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    var baseUrl: String = "https://devwook.me/"

    val TIMEOUT: Long = 15

    var mOKHttpClient = OkHttpClient()
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val getClient: ApiInterface
        get() {
            mOKHttpClient = OkHttpClient().newBuilder().apply {
                addInterceptor(httpLoggingInterceptor)
                connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                readTimeout(TIMEOUT, TimeUnit.SECONDS)
            }.build()

            val retrofit = Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(mOKHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
}
