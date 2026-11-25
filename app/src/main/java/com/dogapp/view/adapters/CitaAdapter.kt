package com.dogapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dogapp.R
import com.dogapp.data.local.entity.Cita
import com.dogapp.databinding.ItemCitaBinding

/**
 * Adaptador de RecyclerView con DataBinding para mostrar la lista de citas.
 */
class CitaAdapter(
    private val onItemClick: (Cita) -> Unit
) : ListAdapter<Cita, CitaAdapter.CitaViewHolder>(CitaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CitaViewHolder(
        private val binding: ItemCitaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cita: Cita) {
            binding.cita = cita
            binding.root.setOnClickListener { onItemClick(cita) }

            Glide.with(binding.ivDog.context).clear(binding.ivDog)

            if (!cita.imagenUrl.isNullOrEmpty()) {
                Glide.with(binding.ivDog.context)
                    .load(cita.imagenUrl)
                    .circleCrop()
                    .placeholder(R.drawable.perro)
                    .error(R.drawable.perro)
                    .into(binding.ivDog)
            } else {
                Glide.with(binding.ivDog.context)
                    .load(R.drawable.perro)
                    .circleCrop()
                    .into(binding.ivDog)
            }

            binding.executePendingBindings()
        }
    }

    class CitaDiffCallback : DiffUtil.ItemCallback<Cita>() {
        override fun areItemsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem == newItem
        }
    }
}