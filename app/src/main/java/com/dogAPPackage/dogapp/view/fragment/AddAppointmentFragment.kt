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
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAppointmentFragment : Fragment() {

    private lateinit var binding: FragmentNuevaCitaBinding
    private val viewModel: AppointmentViewModel by viewModels()
    private var imageUrlObserver: Observer<String?>? = null

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

        checkFields()
        binding.etNombreMascota.filters = arrayOf(InputFilter.LengthFilter(15))
        binding.actvRaza.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etNombrePropietario.filters = arrayOf(InputFilter.LengthFilter(30))
        binding.etTelefono.filters = arrayOf(InputFilter.LengthFilter(10))
        viewModel.getBreedsFromApi()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addAppointmentFragment_to_homeAppointmentFragment)
        }

        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.actvRaza.setAdapter(adapter)
        }

        val sintomas = resources.getStringArray(R.array.sintomas)
        val sintomasAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomas)
        binding.spinnerSintomas.setAdapter(sintomasAdapter)
        binding.spinnerSintomas.setText(sintomas[0], false)

        binding.etNombreMascota.addTextChangedListener(validationTextWatcher)
        binding.actvRaza.addTextChangedListener(validationTextWatcher)
        binding.etNombrePropietario.addTextChangedListener(validationTextWatcher)
        binding.etTelefono.addTextChangedListener(validationTextWatcher)
        binding.spinnerSintomas.addTextChangedListener(validationTextWatcher)
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

        binding.btnGuardar.isEnabled = petName.isNotBlank() && breed.isNotBlank() &&
                ownerName.isNotBlank() && phone.isNotBlank()

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

        if (symptom.isEmpty() || symptom == "Síntomas") {
            Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
            return
        }

        val defaultImageUrl = "https://example.com/default_dog.jpg"
        val appointment = Appointment(
            petName = petName,
            breed = breed,
            ownerName = ownerName,
            phone = phone,
            symptom = symptom,
            imageUrl = defaultImageUrl
        )

        // 2. Intentar obtener mejor imagen (opcional)
        imageUrlObserver?.let { viewModel.imageUrl.removeObserver(it) }

        imageUrlObserver = Observer { imageUrl ->
            // Actualizar la imagen si se obtuvo una mejor
            val finalAppointment = if (!imageUrl.isNullOrEmpty()) {
                appointment.copy(imageUrl = imageUrl)
            } else {
                appointment // Mantener la por defecto
            }

            viewModel.saveAppointment(finalAppointment).observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Cita guardada con éxito", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_addAppointmentFragment_to_homeAppointmentFragment)
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la cita", Toast.LENGTH_SHORT)
                        .show()
                }
                viewModel.imageUrl.removeObserver(imageUrlObserver!!)
            }
        }

        viewModel.imageUrl.observe(viewLifecycleOwner, imageUrlObserver!!)
        viewModel.getRandomImageByBreed(breed)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar observers cuando el fragmento se destruye
        imageUrlObserver?.let { viewModel.imageUrl.removeObserver(it) }
    }
}