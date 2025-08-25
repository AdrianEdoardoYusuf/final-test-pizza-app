package com.vdi.pizzaapp.data.remote

import com.google.gson.annotations.SerializedName

data class getVersionResponse(
    @SerializedName("data")
    val data: List<VersionResponse>
)