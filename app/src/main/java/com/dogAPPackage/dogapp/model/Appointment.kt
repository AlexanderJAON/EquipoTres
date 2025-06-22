package com.dogAPPackage.dogapp.model

data class Appointment(
    var id: String = "", // Cambiado de Int a String para Firebase
    val breed: String = "",
    val imageUrl: String = "",
    val ownerName: String = "",
    val petName: String = "",
    val phone: String = "",
    val symptoms: String = "",

)