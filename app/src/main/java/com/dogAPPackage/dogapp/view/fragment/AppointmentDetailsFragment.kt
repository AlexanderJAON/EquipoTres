package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.databinding.FragmentAppointmentDetailsBinding
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AppointmentDetailsFragment : Fragment() {

    private var _binding: FragmentAppointmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by activityViewModels()

    private var appointmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentId = arguments?.getInt("appointmentId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getAppointmentById(appointmentId)

        viewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            if (appointment != null) {
                if (appointment != null) {
                    binding.toolbarTitle.text = appointment.petName

                    Glide.with(requireContext())
                        .load(appointment.imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imagePet)

                    binding.textRaza.text = appointment.breed
                    binding.textSintoma.text = appointment.symptom
                    binding.textTurno.text = appointment.id.toString()
                    binding.textPropietario.text = appointment.ownerName
                    binding.textTelefono.text = appointment.phone

                    // Botón editar
                    binding.fabEdit.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("appointmentId", appointment.id)
                        }
                        findNavController().navigate(
                            R.id.action_appointmentDetailsFragment_to_appointmentEditFragment,
                            bundle
                        )
                    }

                    // Botón eliminar (sin diálogo para cumplir con el criterio 4)
                    binding.fabDelete.setOnClickListener {
                        viewModel.deleteAppointment(appointment)
                        findNavController().navigate(R.id.action_appointmentDetailsFragment_to_homeFragment)
                    }

                    // Botón flecha atrás
                    binding.backButton.setOnClickListener {
                        findNavController().navigate(R.id.action_appointmentDetailsFragment_to_homeFragment)
                    }
                }

                // Botón eliminar (sin diálogo para cumplir con el criterio 4)
                binding.fabDelete.setOnClickListener {
                    viewModel.deleteAppointment(appointment)
                    findNavController().navigate(R.id.action_appointmentDetailsFragment_to_appointmentEditFragment)
                }

                // Botón flecha atrás
                binding.backButton.setOnClickListener {
                    findNavController().navigate(R.id.action_appointmentDetailsFragment_to_homeFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}