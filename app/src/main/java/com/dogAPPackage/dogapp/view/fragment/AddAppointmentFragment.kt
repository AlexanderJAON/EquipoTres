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
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.dogAPPackage.dogapp.R
import android.text.TextWatcher
import android.text.Editable
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.text.InputFilter



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

        binding.etNombreMascota.filters = arrayOf(InputFilter.LengthFilter(15))
        binding.actvRaza.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etNombrePropietario.filters = arrayOf(InputFilter.LengthFilter(30))
        binding.etTelefono.filters = arrayOf(InputFilter.LengthFilter(10))
        viewModel.getBreedsFromApi()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addAppointmentFragment_to_homeFragment)
        }

        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.actvRaza.setAdapter(adapter)
        }

        val sintomas = resources.getStringArray(R.array.sintomas)
        val sintomasAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomas)
        binding.spinnerSintomas.setAdapter(sintomasAdapter)
        binding.spinnerSintomas.setText(sintomas[0], false)

        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl -> }

        binding.etNombreMascota.addTextChangedListener(validationTextWatcher)
        binding.actvRaza.addTextChangedListener(validationTextWatcher)
        binding.etNombrePropietario.addTextChangedListener(validationTextWatcher)
        binding.etTelefono.addTextChangedListener(validationTextWatcher)
        binding.spinnerSintomas.addTextChangedListener(validationTextWatcher)
        checkFields()
        binding.btnGuardar.setOnClickListener {
            guardarCita()
        }
    }

    private val validationTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkFields()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkFields() {
        val petName = binding.etNombreMascota.text.toString()
        val breed = binding.actvRaza.text.toString()
        val ownerName = binding.etNombrePropietario.text.toString()
        val phone = binding.etTelefono.text.toString()
        val symptom = binding.spinnerSintomas.text.toString()

        binding.btnGuardar.isEnabled = petName.isNotBlank() && breed.isNotBlank() &&
                ownerName.isNotBlank() && phone.isNotBlank() &&
                symptom != "Síntomas"

        if (binding.btnGuardar.isEnabled) {
            binding.btnGuardar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
            binding.btnGuardar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.btnGuardar.setTypeface(null, Typeface.BOLD)
        } else {
            binding.btnGuardar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
            binding.btnGuardar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            binding.btnGuardar.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun guardarCita() {
        val petName = binding.etNombreMascota.text.toString()
        val breed = binding.actvRaza.text.toString()
        val ownerName = binding.etNombrePropietario.text.toString()
        val phone = binding.etTelefono.text.toString()
        val symptom = binding.spinnerSintomas.text.toString()

        if (symptom.isEmpty() || symptom == "Selecciona un síntoma") {
            Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.getRandomImageByBreed(breed)

        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl ->
            val imageUrl = imageUrl ?: ""

            if (petName.isBlank() || breed.isBlank() || ownerName.isBlank() || phone.isBlank() || symptom == "Síntomas") {
                Toast.makeText(requireContext(), "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@observe
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

            findNavController().navigate(R.id.action_addAppointmentFragment_to_homeFragment)
        }
    }
}
