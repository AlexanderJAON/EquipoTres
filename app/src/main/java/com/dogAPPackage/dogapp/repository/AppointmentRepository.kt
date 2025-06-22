package com.dogAPPackage.dogapp.repository

import android.content.Context
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.webservice.ApiService
import com.dogAPPackage.dogapp.webservice.ApiUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppointmentRepository(val context: Context) {
    private var apiService: ApiService = ApiUtils.getApiService()
    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    private val appointmentsRef = db.collection("appointments")
    private val countersRef = db.collection("counters") // Nueva colección para contadores
    // Firebase operations
    suspend fun saveAppointment(appointment: Appointment): String {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Obtener el próximo ID numérico (transacción atómica)
                val nextId = db.runTransaction { transaction ->
                    val counterDoc = transaction.get(countersRef.document("appointments_counter"))
                    var count = 1L

                    if (counterDoc.exists()) {
                        count = counterDoc.getLong("last_id")?.plus(1) ?: 1L
                    }

                    transaction.set(countersRef.document("appointments_counter"),
                        mapOf("last_id" to count))
                    count
                }.await()

                // 2. Guardar la cita con ID numérico
                val appointmentWithId = appointment.copy(id = nextId.toString())
                appointmentsRef.document(nextId.toString()).set(appointmentWithId).await()

                nextId.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    // Obtener todas las citas como Flow
    fun getAllAppointments(): Flow<List<Appointment>> = callbackFlow {
        listenerRegistration = appointmentsRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val appointments = mutableListOf<Appointment>()
                snapshot?.documents?.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointment?.id = document.id // Asignar el ID del documento
                    appointment?.let { appointments.add(it) }
                }
                trySend(appointments)
            }

        awaitClose {
            listenerRegistration?.remove()
        }
    }.flowOn(Dispatchers.IO)

    // Versión suspendida tradicional
    suspend fun getListAppointment(): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = appointmentsRef.get().await()
                snapshot.documents.mapNotNull { document ->
                    document.toObject(Appointment::class.java)?.apply {
                        id = document.id
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Eliminar una cita
    suspend fun deleteAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentsRef.document(appointment.id).delete().await()
        }
    }

    // Actualizar una cita
    suspend fun updateAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentsRef.document(appointment.id).set(appointment).await()
        }
    }

    // Obtener una cita por ID
    suspend fun getAppointmentById(id: String): Appointment? {
        return withContext(Dispatchers.IO) {
            try {
                val document = appointmentsRef.document(id).get().await()
                if (document.exists()) {
                    document.toObject(Appointment::class.java)?.also {
                        it.id = document.id // Asignación directa al var
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    // API operations for dog breeds
    suspend fun getAllBreeds(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllBreeds()
                response.message.keys.toList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getRandomImageByBreed(breed: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRandomImageByBreed(breed)
                response.message
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }


}