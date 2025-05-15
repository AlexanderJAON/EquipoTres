package com.dogAPPackage.dogapp.view.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import android.text.TextWatcher
import android.text.Editable
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.viewmodel.AppointmentViewModel
import com.dogAPPackage.dogapp.webservice.ApiService
import com.dogAPPackage.dogapp.webservice.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppointmentEditFragment : Fragment() {

    private val viewModel: AppointmentViewModel by viewModels()
    private var appointmentId: Int = 10 //precargar los datos de la cita a editar

    private lateinit var mascotaName: EditText
    private lateinit var razaAutoComplete: AutoCompleteTextView
    private lateinit var propietarioName: EditText
    private lateinit var telefono: EditText
    private lateinit var btnEditarCita: Button
    private lateinit var imageViewBreed: ImageView
    private lateinit var backButton: ImageView

    private lateinit var breedsList: List<String>
    private var selectedImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appointmentId = it.getInt("appointmentId", appointmentId)
        }

        if (appointmentId != -1) {
            viewModel.getListAppointments()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mascotaName = view.findViewById(R.id.mascotaName)
        razaAutoComplete = view.findViewById(R.id.razaAutoComplete)
        propietarioName = view.findViewById(R.id.propietarioName)
        telefono = view.findViewById(R.id.telefono)
        btnEditarCita = view.findViewById(R.id.btnEditarCita)
        imageViewBreed = view.findViewById(R.id.imageViewBreed)
        backButton = view.findViewById(R.id.backButton)

        // Cargar cita por ID

        viewModel.listAppointments.observe(viewLifecycleOwner) { list ->
            val appointment = list.find { it.id == appointmentId }
            appointment?.let {
                mascotaName.setText(it.petName)
                razaAutoComplete.setText(it.breed)
                propietarioName.setText(it.ownerName)
                telefono.setText(it.phone)
                selectedImageUrl = it.imageUrl.orEmpty()

                Glide.with(this).load(it.imageUrl).into(imageViewBreed)
                btnEditarCita.isEnabled = true
            }
        }

        btnEditarCita.setOnClickListener {
            val updatedAppointment = Appointment(
                id = appointmentId,
                petName = mascotaName.text.toString(),
                breed = razaAutoComplete.text.toString(),
                ownerName = propietarioName.text.toString(),
                phone = telefono.text.toString(),
                symptom = "", // Si tienes el campo en la vista, reemplázalo
                imageUrl = selectedImageUrl
            )
            viewModel.updateAppointment(updatedAppointment)
            Toast.makeText(requireContext(), "Cita actualizada", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_appointmentEditFragment_to_homeAppointmentFragment) // Ir a la pantalla de inicio (Home)
        }

        // Manejo del botón de retroceso
        /*backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
*/
        // Cargar razas y configurar selección
        fetchBreeds()

        // Validaciones en tiempo real
        mascotaName.addTextChangedListener(inputWatcher)
        razaAutoComplete.addTextChangedListener(inputWatcher)
        propietarioName.addTextChangedListener(inputWatcher)
        telefono.addTextChangedListener(inputWatcher)
    }

    // Observador de texto para validaciones
    private val inputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = validateFields()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun validateFields() {
        val isValid = mascotaName.text.isNotEmpty()
                && razaAutoComplete.text.isNotEmpty()
                && propietarioName.text.isNotEmpty()
                && telefono.text.isNotEmpty()
        btnEditarCita.isEnabled = isValid

        btnEditarCita.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.white)
        )
        btnEditarCita.setTypeface(null, Typeface.BOLD)

    }

    private fun fetchBreeds() {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.getRetrofit().create(ApiService::class.java)
                val response = withContext(Dispatchers.IO) {
                    apiService.getAllBreeds()
                }
                breedsList = response.message.keys.toList()

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    breedsList
                )
                razaAutoComplete.setAdapter(adapter)

                razaAutoComplete.setOnItemClickListener { _, _, position, _ ->
                    val selectedBreed = breedsList[position]
                    loadBreedImage(selectedBreed)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar las razas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBreedImage(breed: String) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.getRetrofit().create(ApiService::class.java)
                val response = withContext(Dispatchers.IO) {
                    apiService.getRandomImageByBreed(breed)
                }

                if (response.status == "success") {
                    selectedImageUrl = response.message
                    Glide.with(requireContext()).load(selectedImageUrl).into(imageViewBreed)
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar imagen", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}