package com.dogapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogapp.R
import com.dogapp.databinding.FragmentHomeBinding
import com.dogapp.view.adapters.CitaAdapter
import com.dogapp.viewmodel.CitaViewModel

/**
 * Fragment principal que muestra la lista de citas registradas.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()
    private lateinit var citaAdapter: CitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.curvedToolbar.configureAsHomeHeader()
        binding.curvedToolbar.setTitle("Administrador\n   de Citas")
        binding.curvedToolbar.hideBackButton()

        setupRecyclerView()

        binding.fabAddCita.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_nuevaCitaFragment)
        }

        citaViewModel.allCitas.observe(viewLifecycleOwner) { citas ->
            val lista = citas ?: emptyList()
            citaAdapter.submitList(lista)

            if (lista.isEmpty()) {
                binding.rvCitas.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                binding.rvCitas.visibility = View.VISIBLE
                binding.tvEmptyState.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        citaAdapter = CitaAdapter { cita ->
            val bundle = Bundle().apply {
                putInt("citaId", cita.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detalleCitaFragment, bundle)
        }
        binding.rvCitas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = citaAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
