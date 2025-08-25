package com.vdi.pizzaapp.data.remote

import kotlinx.coroutines.withTimeout
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val certificatePinner = CertificatePinner.Builder()
        .add("innodev.vnetcloud.com","sha256/1LzopQF/FOcVsVxj2MSOfyDR0J3srWYf9Hl2/GUhxQE=")
        .build()
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .certificatePinner(certificatePinner)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://innodev.vnetcloud.com/threatcast-rml-be/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val api: Apiservice = retrofit.create(Apiservice::class.java)
}