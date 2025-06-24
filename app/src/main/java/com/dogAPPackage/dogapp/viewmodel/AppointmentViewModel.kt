package com.dogAPPackage.dogapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    private val _listAppointments = MutableLiveData<List<Appointment>>()
    val listAppointments: LiveData<List<Appointment>> get() = _listAppointments

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _appointment = MutableLiveData<Appointment?>()
    val appointment: LiveData<Appointment?> get() = _appointment

    private val _breedsList = MutableLiveData<List<String>>()
    val breedsList: LiveData<List<String>> get() = _breedsList

    private val _operationSuccess = MutableLiveData<Boolean?>()
    val operationSuccess: LiveData<Boolean?> get() = _operationSuccess

    private val _appointmentsFlow = MutableStateFlow<List<Appointment>>(emptyList())
    val appointmentsFlow: StateFlow<List<Appointment>> = _appointmentsFlow.asStateFlow()

    fun getBreedsFromApi() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _breedsList.value = appointmentRepository.getAllBreeds()
            } catch (e: Exception) {
                _breedsList.value = emptyList()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getRandomImageByBreed(breed: String) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val imageUrl = appointmentRepository.getRandomImageByBreed(breed)
                _imageUrl.value = imageUrl
            } catch (e: Exception) {
                _imageUrl.value = null
            } finally {
                _progressState.value = false
            }
        }
    }

    fun saveAppointment(appointment: Appointment): LiveData<Boolean> {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val id = appointmentRepository.saveAppointment(appointment)
                _saveResult.value = id.isNotEmpty()
            } catch (e: Exception) {
                _saveResult.value = false
                Log.e("AppointmentVM", "Error saving appointment", e)
            } finally {
                _progressState.value = false
            }
        }
        return saveResult
    }

    fun getAppointmentById(id: String) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _appointment.value = appointmentRepository.getAppointmentById(id)
            } catch (e: Exception) {
                _appointment.value = null
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getListAppointments() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val appointments = appointmentRepository.getListAppointment()
                _listAppointments.value = appointments
            } catch (e: Exception) {
                _listAppointments.value = emptyList()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                appointmentRepository.deleteAppointment(appointment)
                _operationSuccess.value = true
            } catch (e: Exception) {
                _operationSuccess.value = false
            } finally {
                _progressState.value = false
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                appointmentRepository.updateAppointment(appointment)
                _operationSuccess.value = true
            } catch (e: Exception) {
                _operationSuccess.value = false
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getAppointmentsFlow(): Flow<List<Appointment>> {
        return appointmentRepository.getAllAppointments()
    }

    fun refreshAppointments() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _appointmentsFlow.emit(appointmentRepository.getListAppointment())
            } catch (e: Exception) {
                _appointmentsFlow.emit(emptyList())
            } finally {
                _progressState.value = false
            }
        }
    }


    fun resetOperationSuccess() {
        _operationSuccess.value = null
    }

}