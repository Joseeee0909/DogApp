package com.dogapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dogapp.R
import com.dogapp.data.local.entity.Cita
import com.dogapp.databinding.FragmentDetalleCitaBinding
import com.dogapp.viewmodel.CitaViewModel

/**
 * Fragment para visualizar el detalle de una cita existente (HU 4.0).
 * P5-01: diseño del detalle, P5-02: carga de la cita desde Room, P5-03: acciones eliminar y editar.
 */
class DetalleCitaFragment : Fragment() {

    private var _binding: FragmentDetalleCitaBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()
    private var citaId: Int = -1
    private var citaActual: Cita? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        citaId = arguments?.getInt("citaId", -1) ?: -1

        binding.curvedToolbar.setTitle("")
        binding.curvedToolbar.showBackButton {
            findNavController().navigateUp()
        }

        if (citaId == -1) {
            mostrarCitaNoExiste()
            return
        }

        setupListeners()
        observeViewModel()

        // P5-02: consultar la cita con el ID recibido desde Home
        citaViewModel.getCitaById(citaId)
    }

    private fun observeViewModel() {
        citaViewModel.selectedCita.observe(viewLifecycleOwner) { cita ->
            if (cita == null) {
                mostrarCitaNoExiste()
            } else {
                citaActual = cita
                mostrarCita(cita)
            }
        }

        citaViewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    citaViewModel.resetOperationStatus()
                    // P5-03: después de eliminar se regresa al Home
                    findNavController().navigate(R.id.action_detalleCitaFragment_to_homeFragment)
                }
                false -> {
                    citaViewModel.resetOperationStatus()
                    binding.fabEliminar.isEnabled = true
                    Toast.makeText(requireContext(), R.string.msg_error_eliminar, Toast.LENGTH_SHORT).show()
                }
                null -> {}
            }
        }
    }

    private fun mostrarCita(cita: Cita) {
        binding.curvedToolbar.setTitle(cita.nombreMascota)
        binding.tvTurno.text = getString(R.string.label_turno, cita.id)
        binding.tvRaza.text = cita.raza
        binding.tvSintoma.text = cita.sintoma
        binding.tvPropietario.text = getString(R.string.label_propietario, cita.nombrePropietario)
        binding.tvTelefono.text = getString(R.string.label_telefono, cita.telefono)

        Glide.with(this)
            .load(if (cita.imagenUrl.isNullOrEmpty()) R.drawable.perro else cita.imagenUrl)
            .centerCrop()
            .placeholder(R.drawable.perro)
            .error(R.drawable.perro)
            .into(binding.ivMascota)
    }

    private fun setupListeners() {
        // P5-03: FAB eliminar, borra la cita de Room y vuelve al Home
        binding.fabEliminar.setOnClickListener {
            val cita = citaActual ?: return@setOnClickListener
            binding.fabEliminar.isEnabled = false
            citaViewModel.deleteCita(cita)
        }

        // P5-03: FAB editar, abre EditarCitaFragment con el ID de la cita
        binding.fabEditar.setOnClickListener {
            if (citaActual == null) return@setOnClickListener
            if (findNavController().currentDestination?.id == R.id.detalleCitaFragment) {
                val bundle = Bundle().apply {
                    putInt("citaId", citaId)
                }
                findNavController().navigate(
                    R.id.action_detalleCitaFragment_to_editarCitaFragment,
                    bundle
                )
            }
        }
    }

    private fun mostrarCitaNoExiste() {
        Toast.makeText(requireContext(), R.string.msg_cita_no_existe, Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
