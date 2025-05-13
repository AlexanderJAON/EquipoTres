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