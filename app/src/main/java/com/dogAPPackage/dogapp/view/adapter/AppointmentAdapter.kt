package com.dogAPPackage.dogapp.view.adapter


import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dogAPPackage.dogapp.databinding.ItemAppointmentBinding
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.view.viewholder.AppointmentViewHolder
import com.dogAPPackage.dogapp.R

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val navController: NavController
) : RecyclerView.Adapter<AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("appointmentId", appointment.id)
            }
            navController.navigate(R.id.action_homeAppointmentFragment_to_appointmentDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int = appointments.size
}
