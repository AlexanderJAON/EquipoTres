package com.dogAPPackage.dogapp.repository

import android.content.Context
import com.dogAPPackage.dogapp.data.AppointmentDB
import com.dogAPPackage.dogapp.data.AppointmentDao
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.model.AppointmentModelResponse
import com.dogAPPackage.dogapp.webservice.ApiService
import com.dogAPPackage.dogapp.webservice.ApiUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository(val context: Context) {
    private var appointmentDao: AppointmentDao = AppointmentDB.getDatabase(context).appointmentDAO()
    private var apiService: ApiService = ApiUtils.getApiService()

    suspend fun saveAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentDao.saveAppointment(appointment)
        }
    }

    suspend fun getListAppointment(): MutableList<Appointment> {
        return withContext(Dispatchers.IO) {
            appointmentDao.getListAppointment()
        }
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentDao.deleteAppointment(appointment)
        }
    }

    suspend fun updateAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentDao.updateAppointment(appointment)
        }
    }

    suspend fun getAppointmentsFromApi(): MutableList<AppointmentModelResponse> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getProducts()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
    }
}