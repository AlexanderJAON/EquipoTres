package com.dogAPPackage.dogapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petName: String,
    val symptom: String,
    val imageUri: String,
    val timestamp: Long
)