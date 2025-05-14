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

    suspend fun getAllBreeds(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllBreeds()
                response.message.keys.toList() // solo las razas
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    suspend fun getRandomImageByBreed(breed: String): String {
        return try {
            val response = apiService.getRandomImageByBreed(breed)
            response.message // Devuelve la URL de la imagen
        } catch (e: Exception) {
            e.printStackTrace()
            ""  // Si ocurre un error, devuelve una cadena vacía
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
                apiService.getAppointments()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
    }

    suspend fun getAppointmentById(id: Int): Appointment? {
        return withContext(Dispatchers.IO) {
            appointmentDao.getAppointmentById(id)
        }
    }


}