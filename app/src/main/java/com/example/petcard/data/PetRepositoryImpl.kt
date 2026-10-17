package com.example.petcard.data

import com.example.petcard.data.local.EventoDao
import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaDao
import com.example.petcard.data.local.MascotaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// PetRepositoryImpl es la implementación real del contrato PetRepository: cada función simplemente le pasa la tarea al DAO correcto (MascotaDao o EventoDao),
// y así los ViewModels quedan aislados de los detalles de la base de datos.

/** Implementación Room del [PetRepository]. */

// : PetRepository: significa “esta clase implementa la interfaz”. Está obligada a escribir todas las funciones del contrato.
// @Inject constructor: igual que en los ViewModels, Hilt crea esta clase y le entrega lo que necesita.
class PetRepositoryImpl @Inject constructor(
    // MascotaDao y EventoDao: son sus dependencias. Un DAO (Data Access Object) es la pieza de Room que habla directamente con la base de datos mediante consultas SQL.
    private val mascotaDao: MascotaDao,
    private val eventoDao: EventoDao,
) : PetRepository {
    // override: indica que estás escribiendo la versión real de una función declarada en la interfaz.
    // = mascotaDao.observarTodas(): es una función de una sola expresión. Simplemente devuelve lo que responde el DAO.
    override fun observarMascotas(): Flow<List<MascotaEntity>> = mascotaDao.observarTodas()
    override fun observarMascota(id: Long): Flow<MascotaEntity?> = mascotaDao.observarPorId(id)
    override suspend fun guardarMascota(mascota: MascotaEntity): Long = mascotaDao.insertar(mascota)
    override suspend fun actualizarMascota(mascota: MascotaEntity) = mascotaDao.actualizar(mascota)
    override suspend fun eliminarMascota(mascota: MascotaEntity) = mascotaDao.eliminar(mascota)

    override fun observarEventos(): Flow<List<EventoEntity>> = eventoDao.observarTodos()
    override fun observarEventosDeMascota(mascotaId: Long): Flow<List<EventoEntity>> =
        eventoDao.observarPorMascota(mascotaId)
    override fun observarPendientes(): Flow<List<EventoEntity>> = eventoDao.observarPendientes()
    override fun observarEventosDelDia(inicioDia: Long, finDia: Long): Flow<List<EventoEntity>> =
        eventoDao.observarPorDia(inicioDia, finDia)
    override suspend fun guardarEvento(evento: EventoEntity): Long = eventoDao.insertar(evento)
    override suspend fun actualizarEvento(evento: EventoEntity) = eventoDao.actualizar(evento)
    override suspend fun eliminarEvento(evento: EventoEntity) = eventoDao.eliminar(evento)
    override suspend fun marcarEventoHecho(id: Long) = eventoDao.marcarHecho(id)
    override suspend fun obtenerEvento(id: Long): EventoEntity? = eventoDao.porId(id)
}
