// AppointmentDetailsFragment.kt actualizado
package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.databinding.FragmentAppointmentDetailsBinding
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentDetailsFragment : Fragment() {

    private var _binding: FragmentAppointmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppointmentViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        val appointmentId = arguments?.getString("appointmentId") ?: ""

        // Simulación de carga ligera para mejorar percepción visual (HU visual refinement)
        Handler(Looper.getMainLooper()).postDelayed({
            setupObservers(appointmentId)
        }, 100)

        setupListeners()
    }

    private fun setupObservers(appointmentId: String) {
        viewModel.getAppointmentById(appointmentId)

        viewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            appointment?.let {
                bindAppointmentData(it)
            } ?: run {
                showErrorAndNavigateBack()
            }
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            success?.let {
                if (!it) {
                    Toast.makeText(
                        requireContext(),
                        "Error al realizar la operación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.resetOperationSuccess()
            }
        }
    }

    private fun bindAppointmentData(appointment: Appointment) {
        binding.appointment = appointment

        Glide.with(requireContext())
            .load(appointment.imageUrl)
            .placeholder(R.drawable.ic_pet_placeholder)
            .into(binding.imagePet)
    }

    private fun showErrorAndNavigateBack() {
        Toast.makeText(
            requireContext(),
            "Cita no disponible. Intenta nuevamente.",
            Toast.LENGTH_SHORT
        ).show()
        navController.navigate(R.id.action_appointmentDetailsFragment_to_homeAppointmentFragment)
    }

    private fun setupListeners() {
        binding.fabDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.fabEdit.setOnClickListener {
            navigateToEditFragment()
        }

        binding.backButton.setOnClickListener {
            navController.navigate(R.id.action_appointmentDetailsFragment_to_homeAppointmentFragment)
        }
    }

    private fun showDeleteConfirmationDialog() {
        viewModel.appointment.value?.let { appointment ->
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_confirmation_title))
                .setMessage(getString(R.string.delete_confirmation_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    deleteAppointment(appointment)
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
    }

    private fun deleteAppointment(appointment: Appointment) {
        viewModel.deleteAppointment(appointment)
        // Mejora de navegación: evita acumulación en backstack
        navController.popBackStack(R.id.homeAppointmentFragment, false)
        Toast.makeText(
            requireContext(),
            getString(R.string.appointment_deleted_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToEditFragment() {
        viewModel.appointment.value?.let {
            val bundle = Bundle().apply {
                putString("appointmentId", it.id)
            }
            navController.navigate(
                R.id.action_appointmentDetailsFragment_to_appointmentEditFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
