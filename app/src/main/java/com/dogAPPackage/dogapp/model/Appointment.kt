package com.dogAPPackage.dogapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petName: String,
    val breed: String,
    val ownerName: String,
    val phone: String,
    val symptom: String,
    val imageUrl: String
)