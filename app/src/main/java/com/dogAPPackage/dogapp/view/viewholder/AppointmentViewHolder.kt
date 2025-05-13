package com.dogAPPackage.dogapp.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.dogAPPackage.dogapp.databinding.ItemAppointmentBinding
import com.dogAPPackage.dogapp.model.Appointment

class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(appointment: Appointment) {
        binding.apply {
            tvPetName.text = appointment.petName
            tvPetId.text = "#${appointment.id}"
            tvSymptom.text = appointment.symptom

            // Puedes añadir más lógica aquí según necesites
        }
    }
}