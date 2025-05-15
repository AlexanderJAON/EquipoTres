package com.dogAPPackage.dogapp.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.databinding.ItemAppointmentBinding
import com.dogAPPackage.dogapp.model.Appointment

class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(appointment: Appointment) {
        binding.apply {
            tvPetName.text = appointment.petName
            tvPetId.text = "#${appointment.id}"
            tvSymptom.text = appointment.symptom


            Glide.with(root.context)
                .load(appointment.imageUrl)
                .placeholder(R.drawable.ic_pet_placeholder)
                .into(ivPetImage)
        }
    }
}