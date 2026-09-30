package com.example.petcard.data

import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio: única puerta de acceso a los datos.
 *
 * REGLA DE EQUIPO: los ViewModels dependen de esta interfaz, jamás del DAO
 * ni de la base de datos directamente (rúbrica: Patrón Repositorio 1.5 pts).
 */
interface PetRepository {
    // --- Mascotas ---
    fun observarMascotas(): Flow<List<MascotaEntity>>
    fun observarMascota(id: Long): Flow<MascotaEntity?>
    suspend fun guardarMascota(mascota: MascotaEntity): Long
    suspend fun actualizarMascota(mascota: MascotaEntity)
    suspend fun eliminarMascota(mascota: MascotaEntity)

    // --- Eventos ---
    fun observarEventos(): Flow<List<EventoEntity>>
    fun observarEventosDeMascota(mascotaId: Long): Flow<List<EventoEntity>>
    fun observarPendientes(): Flow<List<EventoEntity>>
    fun observarEventosDelDia(inicioDia: Long, finDia: Long): Flow<List<EventoEntity>>
    suspend fun guardarEvento(evento: EventoEntity): Long
    suspend fun actualizarEvento(evento: EventoEntity)
    suspend fun eliminarEvento(evento: EventoEntity)
    suspend fun marcarEventoHecho(id: Long)
    suspend fun obtenerEvento(id: Long): EventoEntity?
}
