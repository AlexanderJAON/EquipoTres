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

    // Binding para acceder a las vistas del layout de forma segura
    private var _binding: FragmentAppointmentEditBinding? = null
    private val binding get() = _binding!!

    // ViewModel para manejar la lógica de negocio y datos de la cita
    private lateinit var viewModel: AppointmentViewModel

    // Id de la cita que se va a editar, se obtiene desde los argumentos del fragmento
    private var appointmentId: Int = 0

    // Bandera para saber si el usuario hizo cambios en los campos
    private var hasChanges = false

    // Se infla el layout del fragmento y se inicializa el binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppointmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Después de crear la vista, inicializamos ViewModel, argumentos y configuramos UI y listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener la instancia del ViewModel compartido con la actividad
        viewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]

        // Obtener el ID de la cita que se va a editar desde los argumentos
        appointmentId = arguments?.getInt("appointmentId") ?: 0

        // Configurar UI (filtros de entrada, estado inicial botones)
        setupUI()

        // Configurar los listeners de los campos y botones
        setupListeners()

        // Observar LiveData del ViewModel para actualizar UI con datos
        observeViewModel()

        // Solicitar al ViewModel que cargue las razas desde la API
        viewModel.getBreedsFromApi()

        // Limpiar la imagen anterior y colocar placeholder
        viewModel.imageUrl.value = null
        Glide.with(requireContext()).clear(binding.imageViewBreed)
        binding.imageViewBreed.setImageResource(R.drawable.ic_pet_placeholder)
    }

    // Método para configurar filtros de longitud y estado inicial de botones
    private fun setupUI() {
        binding.apply {
            mascotaName.filters = arrayOf(InputFilter.LengthFilter(15)) // Limita a 15 caracteres
            razaAutoComplete.filters = arrayOf(InputFilter.LengthFilter(20)) // Limita a 20 caracteres
            propietarioName.filters = arrayOf(InputFilter.LengthFilter(30)) // Limita a 30 caracteres
            telefono.filters = arrayOf(InputFilter.LengthFilter(10)) // Limita a 10 caracteres
        }
        // Botón guardar inicia deshabilitado
        updateSaveButtonState(false)
    }

    // Configurar todos los listeners para detectar cambios en campos y botones
    private fun setupListeners() {
        // TextWatcher que detecta cuando cambia texto en cualquier campo y actualiza estado
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFields() // Verificar si todos los campos están llenos
                hasChanges = true // Marcar que hubo cambios para posible lógica futura
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Agregar TextWatcher a todos los campos relevantes
        listOf(binding.mascotaName, binding.razaAutoComplete, binding.propietarioName, binding.telefono)
            .forEach { it.addTextChangedListener(textWatcher) }

        // Cuando el usuario selecciona una raza del dropdown, cargar imagen aleatoria de esa raza
        binding.razaAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedBreed = binding.razaAutoComplete.adapter.getItem(position).toString()
            viewModel.getRandomImageByBreed(selectedBreed)
        }

        // Guardar cita cuando se presiona el botón
        binding.btnEditarCita.setOnClickListener { saveAppointment() }

        // Botón de volver atrás navega al fragmento anterior
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }

    // Observar los datos en el ViewModel para actualizar la UI cuando cambien
    private fun observeViewModel() {
        // Observar lista de razas para mostrar en el dropdown
        viewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.razaAutoComplete.setAdapter(adapter)
        }

        // Observar URL de la imagen para actualizar la imagen mostrada
        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url ?: R.drawable.ic_pet_placeholder) // Si no hay url, usar placeholder
                .into(binding.imageViewBreed)
        }
    }

    // Verificar que todos los campos tengan texto para habilitar o no el botón de guardar
    private fun checkFields() {
        val allFilled = listOf(
            binding.mascotaName,
            binding.razaAutoComplete,
            binding.propietarioName,
            binding.telefono
        ).all { it.text.isNotBlank() }
        updateSaveButtonState(allFilled)
    }

    // Cambia el estado visual y habilitación del botón de guardar según si está habilitado o no
    private fun updateSaveButtonState(enabled: Boolean) = binding.btnEditarCita.apply {
        isEnabled = enabled
        setTypeface(null, if (enabled) Typeface.BOLD else Typeface.NORMAL)
    }

    // Guarda la cita actualizada usando los datos ingresados por el usuario
    private fun saveAppointment() {
        val mascota = binding.mascotaName.text.toString().trim()
        val raza = binding.razaAutoComplete.text.toString().trim()
        val propietario = binding.propietarioName.text.toString().trim()
        val telefono = binding.telefono.text.toString().trim()

        // Tomar la cita original y crear una copia con los datos actualizados
        viewModel.appointment.value?.let { original ->
            val updated = original.copy(
                petName = mascota,
                breed = raza,
                ownerName = propietario,
                phone = telefono,
                imageUrl = viewModel.imageUrl.value ?: original.imageUrl
            )
            // Llamar ViewModel para actualizar la cita
            viewModel.updateAppointment(updated)

            // Mostrar mensaje de éxito
            Toast.makeText(requireContext(), "Cita actualizada", Toast.LENGTH_SHORT).show()

            // Navegar de regreso a la lista de citas
            findNavController().navigate(R.id.action_appointmentEditFragment_to_homeAppointmentFragment)
        }
    }

    // Limpiar binding para evitar memory leaks cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
