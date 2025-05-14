package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogAPPackage.dogapp.databinding.FragmentHomeAppointmentBinding
import com.dogAPPackage.dogapp.view.adapter.AppointmentAdapter
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.dogAPPackage.dogapp.R

class HomeAppointmentFragment : Fragment() {
    private lateinit var binding: FragmentHomeAppointmentBinding
    private val appointmentViewModel: AppointmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAppointmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()
        observadorViewModel()
    }

    private fun controladores() {
        binding.fabAddAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_homeAppointmentFragment_to_addAppointmentFragment)
        }
    }

    private fun observadorViewModel() {
        observerListAppointments()
        observerProgress()
    }

    private fun observerListAppointments() {
        appointmentViewModel.getListAppointments()
        appointmentViewModel.listAppointments.observe(viewLifecycleOwner) { listAppointments ->
            val recycler = binding.recyclerView

            val layoutManager = LinearLayoutManager(context)
            recycler.layoutManager = layoutManager
            val adapter = AppointmentAdapter(listAppointments, findNavController())
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun observerProgress() {
        appointmentViewModel.progressState.observe(viewLifecycleOwner) { status ->
            binding.progressBar.isVisible = status
        }
    }
}