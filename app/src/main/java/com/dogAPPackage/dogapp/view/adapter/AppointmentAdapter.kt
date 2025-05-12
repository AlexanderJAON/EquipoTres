package com.dogAPPackage.dogapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dogAPPackage.dogapp.databinding.ItemAppointmentBinding
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.view.viewholder.AppointmentViewHolder

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    findNavController: NavController
) :
    RecyclerView.Adapter<AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size
}