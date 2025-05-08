package com.dogAPPackage.dogapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.utils.Constants.NAME_BD

@Database(entities = [Appointment::class], version = 1, exportSchema = false)
abstract class AppointmentDB : RoomDatabase() {

    abstract fun appointmentDAO(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppointmentDB? = null

        fun getDatabase(context: Context): AppointmentDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentDB::class.java,
                    NAME_BD
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}