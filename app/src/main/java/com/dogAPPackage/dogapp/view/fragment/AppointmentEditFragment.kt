package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dogAPPackage.dogapp.databinding.FragmentAppointmentEditBinding
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.dogAPPackage.dogapp.R
import android.widget.ArrayAdapter
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import android.graphics.Typeface
import android.text.InputFilter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.bumptech.glide.Glide

class AppointmentEditFragment : Fragment() {

    private var _binding: FragmentAppointmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppointmentViewModel
    private var appointmentId: Int = 0
    private var hasChanges = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]
        appointmentId = arguments?.getInt("appointmentId") ?: 0

        setupUI()
        setupListeners()
        loadBreeds()

        // Limpiar imagen anterior y cargar nueva cita
        viewModel.imageUrl.value = null
        Glide.with(requireContext()).clear(binding.imageViewBreed)
        binding.imageViewBreed.setImageResource(R.drawable.ic_pet_placeholder)

    }

    private fun setupUI() {
        binding.mascotaName.filters = arrayOf(InputFilter.LengthFilter(15))
        binding.razaAutoComplete.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.propietarioName.filters = arrayOf(InputFilter.LengthFilter(30))
        binding.telefono.filters = arrayOf(InputFilter.LengthFilter(10))

        updateSaveButtonState(false)
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFields()
                hasChanges = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.mascotaName.addTextChangedListener(textWatcher)
        binding.razaAutoComplete.addTextChangedListener(textWatcher)
        binding.propietarioName.addTextChangedListener(textWatcher)
        binding.telefono.addTextChangedListener(textWatcher)

        binding.razaAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedBreed = binding.razaAutoComplete.adapter.getItem(position).toString()
            viewModel.getRandomImageByBreed(selectedBreed)
        }

        binding.btnEditarCita.setOnClickListener { saveAppointment() }

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

    }


    private fun loadBreeds() {
        viewModel.getBreedsFromApi()
        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                breeds
            )
            binding.razaAutoComplete.setAdapter(adapter)
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl ->
            imageUrl?.let {
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .into(binding.imageViewBreed)
            }
        }
    }

    private fun checkFields() {
        val fieldsFilled = binding.mascotaName.isNotBlank() &&
                binding.razaAutoComplete.isNotBlank() &&
                binding.propietarioName.isNotBlank() &&
                binding.telefono.isNotBlank()

        updateSaveButtonState(fieldsFilled)
    }

    fun EditText.isNotBlank(): Boolean = this.text?.isNotBlank() ?: false
    fun AutoCompleteTextView.isNotBlank(): Boolean = this.text?.isNotBlank() ?: false

    private fun updateSaveButtonState(enabled: Boolean) {
        binding.btnEditarCita.isEnabled = enabled
        if (enabled) {
            binding.btnEditarCita.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
            binding.btnEditarCita.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.btnEditarCita.setTypeface(null, Typeface.BOLD)
        } else {
            binding.btnEditarCita.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
            binding.btnEditarCita.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            binding.btnEditarCita.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun saveAppointment() {
        val mascota = binding.mascotaName.text.toString().trim()
        val raza = binding.razaAutoComplete.text.toString().trim()
        val propietario = binding.propietarioName.text.toString().trim()
        val telefono = binding.telefono.text.toString().trim()

        viewModel.appointment.value?.let { original ->
            val updatedAppointment = original.copy(
                petName = mascota,
                breed = raza,
                ownerName = propietario,
                phone = telefono,
                imageUrl = viewModel.imageUrl.value ?: original.imageUrl
            )

            viewModel.updateAppointment(updatedAppointment)
            Toast.makeText(requireContext(), "Cita actualizada", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_appointmentEditFragment_to_homeAppointmentFragment)
        }
    }

}