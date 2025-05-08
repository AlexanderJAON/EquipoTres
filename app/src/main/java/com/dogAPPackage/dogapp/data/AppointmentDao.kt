package com.dogAPPackage.dogapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY timestamp ASC")
    fun getAllAppointments(): LiveData<List<AppointmentEntity>>

    @Insert
    suspend fun insert(appointment: AppointmentEntity)
}