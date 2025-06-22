package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogAPPackage.dogapp.databinding.FragmentHomeAppointmentBinding
import com.dogAPPackage.dogapp.view.adapter.AppointmentAdapter
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.dogAPPackage.dogapp.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeAppointmentFragment : Fragment() {
    private lateinit var binding: FragmentHomeAppointmentBinding
    private val viewModel: AppointmentViewModel by viewModels()
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupControls()
        setupObservers()
        loadAppointments()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(emptyList(), findNavController())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeAppointmentFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupControls() {
        binding.fabAddAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_homeAppointmentFragment_to_addAppointmentFragment)
        }
    }

    private fun setupObservers() {
        viewModel.progressState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading

            // Ocultar el progressBar si hay datos
            if (!isLoading && adapter.itemCount > 0) {
                binding.progressBar.isVisible = false
            }
        }

        lifecycleScope.launch {
            viewModel.appointmentsFlow.collectLatest { appointments ->
                adapter.updateAppointments(appointments)

                // Ocultar progressBar cuando lleguen datos
                if (appointments.isNotEmpty()) {
                    binding.progressBar.isVisible = false
                }
            }
        }
    }

    private fun loadAppointments() {
        viewModel.refreshAppointments()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshAppointments()
    }
}