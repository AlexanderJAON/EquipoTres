package com.dogAPPackage.dogapp.model

import com.google.gson.annotations.SerializedName

data class AppointmentModelResponse(
    @SerializedName("id")
    val id:Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("images")
    val image:String
)
