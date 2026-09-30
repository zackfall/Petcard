package com.example.petcard.data

import com.example.petcard.data.local.EventoDao
import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaDao
import com.example.petcard.data.local.MascotaEntity
import kotlinx.coroutines.flow.Flow

/** Implementación Room del [PetRepository]. */
class PetRepositoryImpl(
    private val mascotaDao: MascotaDao,
    private val eventoDao: EventoDao,
) : PetRepository {
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
