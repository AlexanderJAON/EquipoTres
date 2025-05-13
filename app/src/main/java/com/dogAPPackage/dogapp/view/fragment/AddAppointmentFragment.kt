package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dogAPPackage.dogapp.databinding.FragmentNuevaCitaBinding
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel

class AddAppointmentFragment : Fragment() {

    private lateinit var binding: FragmentNuevaCitaBinding
    private val viewModel: AppointmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNuevaCitaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGuardar.setOnClickListener {
            guardarCita()
        }

    }

    private fun guardarCita() {
        val petName = binding.etNombreMascota.text.toString()
        val breed = binding.actvRaza.text.toString()
        val ownerName = binding.etNombrePropietario.text.toString()
        val phone = binding.etTelefono.text.toString()
        val symptom = binding.spinnerSintomas.text.toString()
        val imageUrl = "" // Aquí puedes poner una URL fija o agregar lógica para subir imagen

        if (petName.isBlank() || breed.isBlank() || ownerName.isBlank() || phone.isBlank() ) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val appointment = Appointment(
            petName = petName,
            breed = breed,
            ownerName = ownerName,
            phone = phone,
            symptom = symptom,
            imageUrl = imageUrl
        )

        viewModel.saveAppointment(appointment)
        Toast.makeText(requireContext(), "Cita guardada con éxito", Toast.LENGTH_SHORT).show()
    }

}
