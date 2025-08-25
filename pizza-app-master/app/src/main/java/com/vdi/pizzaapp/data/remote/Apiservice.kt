package com.vdi.pizzaapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

interface Apiservice {
    @GET
    suspend fun getVersion(
        @Url url: String = "version?page=0&pageSize=10"
    ): getVersionResponse
}