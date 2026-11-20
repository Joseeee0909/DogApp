package com.dogapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa una cita veterinaria.
 */
@Entity(tableName = "citas")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreMascota: String,
    val raza: String,
    val propietario: String,
    val telefono: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val imagenUrl: String? = null,
    val estado: String = "Pendiente"
)
