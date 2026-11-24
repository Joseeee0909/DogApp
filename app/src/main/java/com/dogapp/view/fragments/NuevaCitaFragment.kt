package com.dogapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dogapp.R
import com.dogapp.databinding.FragmentNuevaCitaBinding
import com.dogapp.viewmodel.CitaViewModel
import com.dogapp.viewmodel.DogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment para el registro de una nueva cita veterinaria.
 * Implementa P4-04 (Validación reactiva y estado del botón) y P4-05 (Guardado en Room con imagen dinámica).
 */
class NuevaCitaFragment : Fragment() {

    private var _binding: FragmentNuevaCitaBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.curvedToolbar.setTitle("Nueva Cita")
        binding.curvedToolbar.showBackButton {
            findNavController().popBackStack()
        }

        setupSpinnersAndAutoComplete()
        setupValidation()
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinnersAndAutoComplete() {
        // Cargar razas desde la API Dog CEO (P4-02)
        dogViewModel.fetchAllBreeds()

        dogViewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.actvRaza.setAdapter(adapter)
            binding.actvRaza.threshold = 2
        }

        dogViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Síntomas (P4-03)
        val sintomasList = resources.getStringArray(R.array.lista_sintomas).toList()
        val sintomasAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomasList)
        binding.spinnerSintomas.setAdapter(sintomasAdapter)
        binding.spinnerSintomas.setText("Síntomas", false)
        binding.spinnerSintomas.setOnClickListener {
            binding.spinnerSintomas.showDropDown()
        }
        binding.spinnerSintomas.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.spinnerSintomas.showDropDown()
            }
        }
    }

    /**
     * P4-04: Validación reactiva en tiempo real para habilitar/deshabilitar el botón Guardar Cita.
     */
    private fun setupValidation() {
        val watcher = {
            val mascota = binding.etNombreMascota.text.toString().trim()
            val raza = binding.actvRaza.text.toString().trim()
            val propietario = binding.etNombrePropietario.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            // Validación robusta: campos no vacíos y teléfono con dígitos
            val cleanPhone = telefono.filter { it.isDigit() }
            val isValid = mascota.isNotEmpty() && raza.isNotEmpty() && propietario.isNotEmpty() && cleanPhone.isNotEmpty()

            binding.btnGuardarCita.isEnabled = isValid
        }

        binding.etNombreMascota.doAfterTextChanged { watcher() }
        binding.actvRaza.doAfterTextChanged { watcher() }
        binding.etNombrePropietario.doAfterTextChanged { watcher() }
        binding.etTelefono.doAfterTextChanged { watcher() }

        // Evaluar estado inicial
        watcher()
    }

    private fun setupListeners() {
        binding.btnGuardarCita.setOnClickListener {
            // Criterio 8: Validación estricta del síntoma al hacer clic
            val sintoma = binding.spinnerSintomas.text.toString().trim()
            if (sintoma.isEmpty() || sintoma == "Síntomas") {
                Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mascota = binding.etNombreMascota.text.toString().trim()
            val raza = binding.actvRaza.text.toString().trim()
            val propietario = binding.etNombrePropietario.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val currentTime = timeFormat.format(Date())

            // Deshabilitar botón durante el proceso de guardado y consulta de red
            binding.btnGuardarCita.isEnabled = false

            // Llamada al ViewModel para persistir con imagen dinámica de la raza (P4-05)
            citaViewModel.insertCitaWithDynamicImage(
                nombreMascota = mascota,
                raza = raza,
                nombrePropietario = propietario,
                telefono = telefono,
                fecha = currentDate,
                hora = currentTime,
                sintoma = sintoma
            )
        }
    }

    private fun observeViewModel() {
        citaViewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    citaViewModel.resetOperationStatus()
                    // Volver al Home Administrador de Citas de forma limpia haciendo pop de la pila de navegación
                    try {
                        findNavController().popBackStack()
                    } catch (e: Exception) {
                        findNavController().navigateUp()
                    }
                }
                false -> {
                    citaViewModel.resetOperationStatus()
                    Toast.makeText(requireContext(), "Error al guardar la cita. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    // Re-evaluar validación para restaurar estado del botón
                    val mascota = binding.etNombreMascota.text.toString().trim()
                    val raza = binding.actvRaza.text.toString().trim()
                    val propietario = binding.etNombrePropietario.text.toString().trim()
                    val telefono = binding.etTelefono.text.toString().trim()
                    val cleanPhone = telefono.filter { it.isDigit() }
                    binding.btnGuardarCita.isEnabled = mascota.isNotEmpty() && raza.isNotEmpty() && propietario.isNotEmpty() && cleanPhone.isNotEmpty()
                }
                null -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
