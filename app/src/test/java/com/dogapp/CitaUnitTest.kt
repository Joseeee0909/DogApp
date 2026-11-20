package com.dogapp

import com.dogapp.data.local.entity.Cita
import com.dogapp.view.adapters.CitaAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CitaUnitTest {

    @Test
    fun citaEntity_initialization_isCorrect() {
        val cita = Cita(
            id = 1,
            nombreMascota = "Firulais",
            raza = "Labrador",
            propietario = "Juan Pérez",
            telefono = "555-1234",
            fecha = "2026-03-20",
            hora = "10:00 AM",
            motivo = "Vacunación",
            imagenUrl = "https://images.dog.ceo/breeds/labrador/n02099712_1234.jpg",
            estado = "Pendiente"
        )

        assertEquals(1, cita.id)
        assertEquals("Firulais", cita.nombreMascota)
        assertEquals("Labrador", cita.raza)
        assertEquals("Juan Pérez", cita.propietario)
        assertEquals("555-1234", cita.telefono)
        assertEquals("2026-03-20", cita.fecha)
        assertEquals("10:00 AM", cita.hora)
        assertEquals("Vacunación", cita.motivo)
        assertEquals("https://images.dog.ceo/breeds/labrador/n02099712_1234.jpg", cita.imagenUrl)
        assertEquals("Pendiente", cita.estado)
    }

    @Test
    fun citaDiffCallback_areItemsTheSame_returnsTrueForSameId() {
        val diffCallback = CitaAdapter.CitaDiffCallback()
        val cita1 = Cita(id = 10, nombreMascota = "Max", raza = "Poodle", propietario = "Ana", telefono = "123", fecha = "2026-01-01", hora = "09:00", motivo = "Revisión")
        val cita2 = Cita(id = 10, nombreMascota = "Max Modificado", raza = "Poodle", propietario = "Ana", telefono = "123", fecha = "2026-01-01", hora = "09:00", motivo = "Revisión")

        assertTrue(diffCallback.areItemsTheSame(cita1, cita2))
    }

    @Test
    fun citaDiffCallback_areItemsTheSame_returnsFalseForDifferentId() {
        val diffCallback = CitaAdapter.CitaDiffCallback()
        val cita1 = Cita(id = 1, nombreMascota = "Max", raza = "Poodle", propietario = "Ana", telefono = "123", fecha = "2026-01-01", hora = "09:00", motivo = "Revisión")
        val cita2 = Cita(id = 2, nombreMascota = "Max", raza = "Poodle", propietario = "Ana", telefono = "123", fecha = "2026-01-01", hora = "09:00", motivo = "Revisión")

        assertFalse(diffCallback.areItemsTheSame(cita1, cita2))
    }
}
