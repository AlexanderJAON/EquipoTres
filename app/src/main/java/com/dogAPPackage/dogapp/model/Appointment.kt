package com.dogAPPackage.dogapp.model

data class Appointment(
    var id: String = "",
    val petName: String = "",
    val breed: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val symptom: String = "",
    val imageUrl: String = ""
) {

    constructor() : this("", "", "", "", "", "", "")
}