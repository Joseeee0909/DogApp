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
import com.dogapp.data.local.entity.Cita
import com.dogapp.databinding.FragmentEditarCitaBinding
import com.dogapp.viewmodel.CitaViewModel
import com.dogapp.viewmodel.DogViewModel

/**
 * Fragment para modificar la información de una cita (HU 5.0).
 * P5-04: formulario precargado con los datos de la cita, P5-05: actualización en Room.
 */
class EditarCitaFragment : Fragment() {

    private var _binding: FragmentEditarCitaBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private var citaId: Int = -1
    private var citaOriginal: Cita? = null
    private var restaurandoEstado = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        citaId = arguments?.getInt("citaId", -1) ?: -1
        restaurandoEstado = savedInstanceState != null

        binding.curvedToolbar.setTitle(getString(R.string.title_editar_cita))
        // Criterio 1: la flecha atrás regresa al Detalle de la Cita
        binding.curvedToolbar.showBackButton {
            findNavController().navigateUp()
        }

        if (citaId == -1) {
            Toast.makeText(requireContext(), R.string.msg_cita_no_existe, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupAutoCompleteRaza()
        setupValidation()
        setupListeners()
        observeViewModel()

        citaViewModel.getCitaById(citaId)
    }

    /**
     * Criterio 3: AutoCompleteTextView de razas consumiendo la API Dog CEO con Retrofit.
     */
    private fun setupAutoCompleteRaza() {
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
    }

    /**
     * P5-04: precarga los datos actuales de la cita en el formulario.
     */
    private fun precargarDatos(cita: Cita) {
        binding.etNombreMascota.setText(cita.nombreMascota)
        binding.actvRaza.setText(cita.raza, false)
        binding.etNombrePropietario.setText(cita.nombrePropietario)
        binding.etTelefono.setText(cita.telefono)
        validarFormulario()
    }

    /**
     * Criterios 6 y 7: el botón Editar Cita solo se habilita con los cuatro campos diligenciados.
     */
    private fun setupValidation() {
        binding.etNombreMascota.doAfterTextChanged { validarFormulario() }
        binding.actvRaza.doAfterTextChanged { validarFormulario() }
        binding.etNombrePropietario.doAfterTextChanged { validarFormulario() }
        binding.etTelefono.doAfterTextChanged { validarFormulario() }

        validarFormulario()
    }

    private fun validarFormulario() {
        val mascota = binding.etNombreMascota.text.toString().trim()
        val raza = binding.actvRaza.text.toString().trim()
        val propietario = binding.etNombrePropietario.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()

        val isValid = citaOriginal != null &&
            mascota.isNotEmpty() &&
            raza.isNotEmpty() &&
            propietario.isNotEmpty() &&
            telefono.isNotEmpty() && telefono.all { it.isDigit() }

        binding.btnEditarCita.isEnabled = isValid
    }

    private fun setupListeners() {
        binding.btnEditarCita.setOnClickListener {
            val original = citaOriginal ?: return@setOnClickListener

            // P5-05: se usa copy() para conservar el ID original y no crear una cita duplicada
            val citaEditada = original.copy(
                nombreMascota = binding.etNombreMascota.text.toString().trim(),
                raza = binding.actvRaza.text.toString().trim(),
                propietario = binding.etNombrePropietario.text.toString().trim(),
                telefono = binding.etTelefono.text.toString().trim()
            )

            binding.btnEditarCita.isEnabled = false
            citaViewModel.updateCitaWithDynamicImage(citaEditada, original.raza)
        }
    }

    private fun observeViewModel() {
        citaViewModel.selectedCita.observe(viewLifecycleOwner) { cita ->
            if (cita == null) {
                Toast.makeText(requireContext(), R.string.msg_cita_no_existe, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else if (citaOriginal == null) {
                citaOriginal = cita
                // Si se rotó la pantalla, el sistema ya restauró lo que el usuario había escrito
                if (restaurandoEstado) validarFormulario() else precargarDatos(cita)
            }
        }

        citaViewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    citaViewModel.resetOperationStatus()
                    // Criterio 8: al editar se navega al Home, donde se ve la cita modificada
                    findNavController().navigate(R.id.action_editarCitaFragment_to_homeFragment)
                }
                false -> {
                    citaViewModel.resetOperationStatus()
                    Toast.makeText(requireContext(), R.string.msg_error_editar, Toast.LENGTH_SHORT).show()
                    validarFormulario()
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
