package com.dogAPPackage.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.databinding.FragmentAppointmentEditBinding
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import android.graphics.Typeface
import android.text.InputFilter
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentEditFragment : Fragment() {

    private var _binding: FragmentAppointmentEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppointmentViewModel by viewModels()
    private var hasChanges = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appointmentId = arguments?.getString("appointmentId") ?: ""

        setupUI()
        setupListeners()
        observeViewModel()

        viewModel.getBreedsFromApi()
        viewModel.getAppointmentById(appointmentId)
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

        listOf(
            binding.mascotaName,
            binding.razaAutoComplete,
            binding.propietarioName,
            binding.telefono
        ).forEach { it.addTextChangedListener(textWatcher) }

        binding.razaAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedBreed = binding.razaAutoComplete.adapter.getItem(position).toString()
            viewModel.getRandomImageByBreed(selectedBreed)
            hasChanges = true
        }

        binding.btnEditarCita.setOnClickListener {
            if (hasChanges) {
                saveAppointment()
            } else {
                Toast.makeText(requireContext(), "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                breeds
            )
            binding.razaAutoComplete.setAdapter(adapter)
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url ?: R.drawable.ic_pet_placeholder)
                .into(binding.imageViewBreed)
        }

        viewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            appointment?.let { bindAppointmentData(it) }
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            success?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Cita actualizada con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_appointmentEditFragment_to_homeAppointmentFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar la cita",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.resetOperationSuccess()
            }
        }
    }

    private fun bindAppointmentData(appointment: Appointment) {
        binding.apply {
            mascotaName.setText(appointment.petName)
            razaAutoComplete.setText(appointment.breed)
            propietarioName.setText(appointment.ownerName)
            telefono.setText(appointment.phone)

            if (!appointment.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(appointment.imageUrl)
                    .into(imageViewBreed)
            }
        }
        checkFields()
    }

    private fun checkFields() {
        val allFieldsFilled = listOf(
            binding.mascotaName.text,
            binding.razaAutoComplete.text,
            binding.propietarioName.text,
            binding.telefono.text
        ).all { it?.isNotBlank() == true }

        updateSaveButtonState(allFieldsFilled)
    }

    private fun updateSaveButtonState(enabled: Boolean) {
        binding.btnEditarCita.apply {
            isEnabled = enabled
            setTypeface(null, if (enabled) Typeface.BOLD else Typeface.NORMAL)
            setTextColor(
                ContextCompat.getColor(
                    context,
                    if (enabled) android.R.color.white else R.color.gray
                )
            )
        }
    }

    private fun saveAppointment() {
        val petName = binding.mascotaName.text.toString().trim()
        val breed = binding.razaAutoComplete.text.toString().trim()
        val ownerName = binding.propietarioName.text.toString().trim()
        val phone = binding.telefono.text.toString().trim()

        viewModel.appointment.value?.let { originalAppointment ->
            val updatedAppointment = originalAppointment.copy(
                petName = petName,
                breed = breed,
                ownerName = ownerName,
                phone = phone,
                imageUrl = viewModel.imageUrl.value ?: originalAppointment.imageUrl
            )
            viewModel.updateAppointment(updatedAppointment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}