package com.dogAPPackage.dogapp.model

data class Appointment(
    val id: Int,
    val petName: String,
    val symptom: String,
    val imageUri: String?,
    val timestamp: Long
)