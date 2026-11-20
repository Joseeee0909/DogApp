package com.dogapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dogapp.data.local.dao.CitaDao
import com.dogapp.data.local.entity.Cita

/**
 * Base de datos principal de Room para la aplicación DogApp.
 */
@Database(entities = [Cita::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dogapp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
