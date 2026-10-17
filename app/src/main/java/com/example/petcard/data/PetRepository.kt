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

// PetRepository es el contrato que lista todo lo que la app puede hacer con mascotas y eventos (observar, guardar, actualizar, eliminar),
//  sin revelar cómo se almacena; los ViewModels piden esta interfaz y otra clase la implementa con Room.
interface PetRepository {
    // --- Mascotas ---
    // No son suspend: devuelven un Flow al instante, que luego se va emitiendo cada vez que los datos cambian.
    fun observarMascotas(): Flow<List<MascotaEntity>>
    // observarMascota(id) devuelve MascotaEntity? (con ?) porque puede que no exista una mascota con ese id,
    // por ejemplo si se borró.
    fun observarMascota(id: Long): Flow<MascotaEntity?>

    //suspend: son operaciones que toman tiempo (escribir en disco) y se ejecutan dentro de una corrutina.
    // Por eso en los ViewModels las llamaste dentro de viewModelScope.launch { ... }.
    // guardarMascota devuelve un Long: el id generado, que usaste en AgregarMascotaEvent.Guardado(id).
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
