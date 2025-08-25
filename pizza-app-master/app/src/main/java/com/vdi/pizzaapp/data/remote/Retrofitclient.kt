package com.vdi.pizzaapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://innodev.vnetcloud.com/threatcast-rml-be/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: Apiservice = retrofit.create(Apiservice::class.java)
}