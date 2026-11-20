package com.dogapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dogapp.data.local.entity.Cita

/**
 * Data Access Object (DAO) para operaciones CRUD sobre la tabla 'citas'.
 */
@Dao
interface CitaDao {

    @Query("SELECT * FROM citas ORDER BY fecha DESC, hora DESC")
    fun getAllCitas(): LiveData<List<Cita>>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    suspend fun getCitaById(id: Int): Cita?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCita(cita: Cita): Long

    @Update
    suspend fun updateCita(cita: Cita): Int

    @Delete
    suspend fun deleteCita(cita: Cita): Int

    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun deleteCitaById(id: Int): Int
}
