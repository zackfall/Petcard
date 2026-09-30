package com.example.petcard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petcard.util.Dates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Base de datos Room: única fuente de verdad (offline-first).
 * Incluye datos semilla (Max y Luna, como en los diseños PDF) para que la
 * demo funcione en el primer arranque.
 */
@Database(
    entities = [MascotaEntity::class, EventoEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class PetCardDatabase : RoomDatabase() {
    abstract fun mascotaDao(): MascotaDao
    abstract fun eventoDao(): EventoDao

    companion object {
        @Volatile
        private var instancia: PetCardDatabase? = null

        fun obtener(contexto: Context): PetCardDatabase =
            instancia ?: synchronized(this) {
                instancia ?: construir(contexto).also { instancia = it }
            }

        private fun construir(contexto: Context): PetCardDatabase =
            Room.databaseBuilder(
                contexto.applicationContext,
                PetCardDatabase::class.java,
                "petcard.db",
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(SemillaCallback())
                .build()
    }

    private class SemillaCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Sembrar fuera del hilo principal.
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                instancia?.let { sembrar(it) }
            }
        }

        private suspend fun sembrar(bd: PetCardDatabase) {
            val maxId = bd.mascotaDao().insertar(
                MascotaEntity(
                    nombre = "Max",
                    especie = "Perro",
                    raza = "Golden Retriever",
                    fechaNacimientoMillis = Dates.inicioDiaMillis(2023, 5, 10),
                    pesoKg = 32.5,
                ),
            )
            val lunaId = bd.mascotaDao().insertar(
                MascotaEntity(
                    nombre = "Luna",
                    especie = "Gato",
                    raza = "Siamés",
                    fechaNacimientoMillis = Dates.inicioDiaMillis(2024, 2, 1),
                    pesoKg = 4.2,
                ),
            )
            val dao = bd.eventoDao()
            dao.insertar(
                EventoEntity(
                    mascotaId = maxId,
                    tipo = TipoEvento.VACUNA,
                    titulo = "Vacuna de la Rabia",
                    fechaMillis = Dates.inicioDiaMillis(2026, 10, 24),
                    horaMinutos = 10 * 60,
                    lugar = "Clínica Veterinaria Principal",
                    notas = "Recordar llevar carnet físico de vacunación.",
                ),
            )
            dao.insertar(
                EventoEntity(
                    mascotaId = lunaId,
                    tipo = TipoEvento.CONTROL_VETERINARIO,
                    titulo = "Chequeo de Peso",
                    fechaMillis = Dates.inicioDiaMillis(2026, 10, 28),
                    horaMinutos = 16 * 60,
                    lugar = "Clínica Veterinaria Principal",
                    notas = "Llevar registro de alimentación diaria.",
                ),
            )
            dao.insertar(
                EventoEntity(
                    mascotaId = maxId,
                    tipo = TipoEvento.CONTROL_VETERINARIO,
                    titulo = "Visita al Veterinario - Control",
                    fechaMillis = Dates.inicioDiaMillis(2026, 10, 15),
                    hecho = true,
                ),
            )
            dao.insertar(
                EventoEntity(
                    mascotaId = lunaId,
                    tipo = TipoEvento.DESPARASITACION,
                    titulo = "Desparasitación interna",
                    fechaMillis = Dates.inicioDiaMillis(2026, 10, 1),
                    hecho = true,
                ),
            )
        }
    }
}
