package com.dogAPPackage.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.model.AppointmentModelResponse
import com.dogAPPackage.dogapp.repository.AppointmentRepository
import kotlinx.coroutines.launch


class AppointmentViewModel(application: Application) : AndroidViewModel(application) {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: MutableLiveData<String?> get() = _imageUrl

    private val context = getApplication<Application>()
    private val appointmentRepository = AppointmentRepository(context)

    private val _listAppointments = MutableLiveData<MutableList<Appointment>>()
    val listAppointments: LiveData<MutableList<Appointment>> get() = _listAppointments

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _listAppointmentsFromApi = MutableLiveData<MutableList<AppointmentModelResponse>>()
    val listAppointmentsFromApi: LiveData<MutableList<AppointmentModelResponse>> get() = _listAppointmentsFromApi

    private val _appointment = MutableLiveData<Appointment?>()
    val appointment: LiveData<Appointment?> get() = _appointment

    private val _breedsList = MutableLiveData<List<String>>()
    val breedsList: LiveData<List<String>> get() = _breedsList

    fun getBreedsFromApi() {
        viewModelScope.launch {
            try {
                _breedsList.value = appointmentRepository.getAllBreeds()
            } catch (e: Exception) {
                _breedsList.value = emptyList()
            }
        }
    }

    fun getRandomImageByBreed(breed: String) {
        viewModelScope.launch {
            try {
                val imageUrl = appointmentRepository.getRandomImageByBreed(breed)
                _imageUrl.value = imageUrl
            } catch (e: Exception) {
                _imageUrl.value = null
            }
        }
    }

    fun getAppointmentById(id: Int) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val result = appointmentRepository.getAppointmentById(id)
                _appointment.value = result
            } catch (e: Exception) {
                _appointment.value = null
            } finally {
                _progressState.value = false
            }
        }
    }

    fun saveAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                appointmentRepository.saveAppointment(appointment)
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }

    fun getListAppointments() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listAppointments.value = appointmentRepository.getListAppointment()
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                appointmentRepository.deleteAppointment(appointment)
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                appointmentRepository.updateAppointment(appointment)
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }

    fun getAppointmentsFromApi() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listAppointmentsFromApi.value = appointmentRepository.getAppointmentsFromApi()
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }
}