package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.databinding.FragmentAppointmentDetailsBinding
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.model.Appointment
import androidx.appcompat.app.AlertDialog

class AppointmentDetailsFragment : Fragment() {

    private var _binding: FragmentAppointmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var navController: NavController
    private var appointmentId: Int = 0

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

        viewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]
        navController = findNavController()

        // Obtener el ID de la cita desde los argumentos
        arguments?.let {
            appointmentId = it.getInt("appointmentId", 0)
        }

        // Cargar datos de la cita
        viewModel.getAppointmentById(appointmentId)

        // Observar los cambios
        viewModel.appointment.observe(viewLifecycleOwner) { appointment: Appointment? ->
            appointment?.let {
                // Asignar datos al layout
                binding.appointment = it

                // Cargar la imagen manualmente con Glide
                Glide.with(requireContext())
                    .load(it.imageUrl)
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .into(binding.imagePet)
            }
        }

        // Botón eliminar (FAB rosa)
        binding.fabDelete.setOnClickListener {
            val appointment = viewModel.appointment.value
            appointment?.let {
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro que deseas eliminar esta cita?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.deleteAppointment(it)
                        navController.navigate(R.id.action_appointmentDetailsFragment_to_homeAppointmentFragment)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }



        // Botón editar
        binding.fabEdit.setOnClickListener {
            viewModel.appointment.value?.let {
                val bundle = Bundle().apply {
                    putInt("appointmentId", it.id)
                }
                navController.navigate(
                    R.id.action_appointmentDetailsFragment_to_appointmentEditFragment,
                    bundle
                )
            }
        }

        // Flecha atrás (toolbar)
        binding.backButton.setOnClickListener {
            navController.navigate(R.id.action_appointmentDetailsFragment_to_homeAppointmentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}