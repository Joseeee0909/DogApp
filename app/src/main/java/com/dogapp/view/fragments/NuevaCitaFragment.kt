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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dogapp.data.local.entity.Cita
import com.dogapp.data.remote.RetrofitClient
import com.dogapp.data.repository.DogRepository
import com.dogapp.databinding.FragmentNuevaCitaBinding
import com.dogapp.viewmodel.CitaViewModel
import com.dogapp.viewmodel.DogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment para el registro de una nueva cita veterinaria.
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
            findNavController().navigateUp()
        }

        setupSpinnersAndAutoComplete()
        setupValidation()
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinnersAndAutoComplete() {
        // Cargar razas desde la API Dog CEO
        dogViewModel.fetchAllBreeds()
        dogViewModel.breedsList.observe(viewLifecycleOwner) { breeds ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
            binding.actvRaza.setAdapter(adapter)
            binding.actvRaza.threshold = 1
        }

        // Síntomas
        val sintomasList = listOf("Síntomas", "Solo duerme", "No come", "Fractura extremidad", "Tiene pulgas", "Tiene garrapatas", "Bota demasiado pelo")
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

    private fun setupValidation() {
        val watcher = {
            val mascota = binding.etNombreMascota.text.toString().trim()
            val raza = binding.actvRaza.text.toString().trim()
            val propietario = binding.etNombrePropietario.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            val isValid = mascota.isNotEmpty() && raza.isNotEmpty() && propietario.isNotEmpty() && telefono.isNotEmpty()
            binding.btnGuardarCita.isEnabled = isValid
        }

        binding.etNombreMascota.doAfterTextChanged { watcher() }
        binding.actvRaza.doAfterTextChanged { watcher() }
        binding.etNombrePropietario.doAfterTextChanged { watcher() }
        binding.etTelefono.doAfterTextChanged { watcher() }
    }

    private fun setupListeners() {
        binding.btnGuardarCita.setOnClickListener {
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

            binding.btnGuardarCita.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val dogRepo = DogRepository(RetrofitClient.dogApiService)
                val imageResult = dogRepo.getRandomImageByBreed(raza)
                val imageUrl = imageResult.getOrNull() ?: dogRepo.getRandomDogImage().getOrNull()

                val cita = Cita(
                    nombreMascota = mascota,
                    raza = raza,
                    propietario = propietario,
                    telefono = telefono,
                    fecha = currentDate,
                    hora = currentTime,
                    motivo = sintoma,
                    imagenUrl = imageUrl,
                    estado = "Pendiente"
                )

                withContext(Dispatchers.Main) {
                    citaViewModel.insertCita(cita)
                }
            }
        }
    }

    private fun observeViewModel() {
        citaViewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            if (status == true) {
                citaViewModel.resetOperationStatus()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
