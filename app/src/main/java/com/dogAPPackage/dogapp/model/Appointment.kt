package com.dogAPPackage.dogapp.model

data class Appointment(
    var id: String = "",  // Firebase usa String como ID
    val petName: String = "",
    val breed: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val symptom: String = "",
    val imageUrl: String = ""
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", "", "", "")
}