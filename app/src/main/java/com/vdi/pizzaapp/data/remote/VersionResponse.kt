package com.vdi.pizzaapp.data.remote

import com.google.gson.annotations.SerializedName

data class VersionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("created_date")
    val createdDate: String,
    @SerializedName("app")
    val app: String,
    @SerializedName("version")
    val version: String
)