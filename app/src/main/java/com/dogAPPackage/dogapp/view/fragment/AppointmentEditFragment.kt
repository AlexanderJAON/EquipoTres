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
import com.bumptech.glide.Glide

class AppointmentEditFragment : Fragment() {

    private var _binding: FragmentAppointmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppointmentViewModel
    private var appointmentId: Int = 0
    private var hasChanges = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppointmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]
        appointmentId = arguments?.getInt("appointmentId") ?: 0
        setupUI()
        setupListeners()
        observeViewModel()
        viewModel.getBreedsFromApi()
        viewModel.imageUrl.value = null
        Glide.with(requireContext()).clear(binding.imageViewBreed)
        binding.imageViewBreed.setImageResource(R.drawable.ic_pet_placeholder)
    }

    private fun setupUI() {
        binding.apply {
            mascotaName.filters = arrayOf(InputFilter.LengthFilter(15))
            razaAutoComplete.filters = arrayOf(InputFilter.LengthFilter(20))
            propietarioName.filters = arrayOf(InputFilter.LengthFilter(30))
            telefono.filters = arrayOf(InputFilter.LengthFilter(10))
        }
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
        listOf(binding.mascotaName, binding.razaAutoComplete, binding.propietarioName, binding.telefono)
            .forEach { it.addTextChangedListener(textWatcher) }

        binding.razaAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedBreed = binding.razaAutoComplete.adapter.getItem(position).toString()
            viewModel.getRandomImageByBreed(selectedBreed)
        }

        binding.btnEditarCita.setOnClickListener { saveAppointment() }
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun observeViewModel() {
        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.razaAutoComplete.setAdapter(adapter)
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url ?: R.drawable.ic_pet_placeholder)
                .into(binding.imageViewBreed)
        }
    }

    private fun checkFields() {
        val allFilled = listOf(
            binding.mascotaName,
            binding.razaAutoComplete,
            binding.propietarioName,
            binding.telefono
        ).all { it.text.isNotBlank() }
        updateSaveButtonState(allFilled)
    }

    private fun updateSaveButtonState(enabled: Boolean) = binding.btnEditarCita.apply {
        isEnabled = enabled
        setTypeface(null, if (enabled) Typeface.BOLD else Typeface.NORMAL)
        val textColor = if (enabled)
            ContextCompat.getColor(context, android.R.color.white)
        else
            ContextCompat.getColor(context, R.color.gray)
        setTextColor(textColor)
    }

    private fun saveAppointment() {
        val mascota = binding.mascotaName.text.toString().trim()
        val raza = binding.razaAutoComplete.text.toString().trim()
        val propietario = binding.propietarioName.text.toString().trim()
        val telefono = binding.telefono.text.toString().trim()

        viewModel.appointment.value?.let { original ->
            val updated = original.copy(
                petName = mascota,
                breed = raza,
                ownerName = propietario,
                phone = telefono,
                imageUrl = viewModel.imageUrl.value ?: original.imageUrl
            )
            viewModel.updateAppointment(updated)
            Toast.makeText(requireContext(), "Cita actualizada", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_appointmentEditFragment_to_homeAppointmentFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
