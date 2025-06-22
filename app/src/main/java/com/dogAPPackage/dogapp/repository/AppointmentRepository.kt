package com.dogAPPackage.dogapp.repository

import android.content.Context
import com.dogAPPackage.dogapp.model.Appointment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppointmentRepository(val context: Context) {
    private val database = FirebaseDatabase.getInstance()
    private val appointmentsRef = database.getReference("appointments")

    // Guardar una nueva cita
    suspend fun saveAppointment(appointment: Appointment): String {
        return withContext(Dispatchers.IO) {
            val newAppointmentRef = appointmentsRef.push()
            appointment.id = newAppointmentRef.key ?: ""
            newAppointmentRef.setValue(appointment).await()
            appointment.id
        }
    }

    // Obtener todas las citas como Flow
    fun getAllAppointments(): Flow<List<Appointment>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<Appointment>()
                for (childSnapshot in snapshot.children) {
                    val appointment = childSnapshot.getValue(Appointment::class.java)
                    appointment?.let { appointments.add(it) }
                }
                trySend(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        appointmentsRef.addValueEventListener(listener)

        awaitClose {
            appointmentsRef.removeEventListener(listener)
        }
    }.flowOn(Dispatchers.IO)

    // Eliminar una cita
    suspend fun deleteAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentsRef.child(appointment.id).removeValue().await()
        }
    }

    // Actualizar una cita
    suspend fun updateAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentsRef.child(appointment.id).setValue(appointment).await()
        }
    }

    // Obtener una cita por ID
    suspend fun getAppointmentById(id: String): Appointment? {
        return withContext(Dispatchers.IO) {
            appointmentsRef.child(id).get().await().getValue(Appointment::class.java)
        }
    }
}