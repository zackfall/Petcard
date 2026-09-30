package com.example.petcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    // Lecturas reactivas: la UI se actualiza sola cuando cambia la BD.
    @Query("SELECT * FROM eventos ORDER BY fechaMillis DESC, horaMinutos DESC")
    fun observarTodos(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE mascotaId = :mascotaId ORDER BY fechaMillis DESC, horaMinutos DESC")
    fun observarPorMascota(mascotaId: Long): Flow<List<EventoEntity>>

    /** Eventos no realizados, ordenados por fecha (pantalla "Eventos pendientes"). */
    @Query("SELECT * FROM eventos WHERE hecho = 0 ORDER BY fechaMillis ASC, horaMinutos ASC")
    fun observarPendientes(): Flow<List<EventoEntity>>

    /** Eventos de un día concreto (pantalla "Calendario" -> eventos para hoy). */
    @Query(
        "SELECT * FROM eventos WHERE fechaMillis >= :inicioDia AND fechaMillis < :finDia " +
            "ORDER BY horaMinutos ASC",
    )
    fun observarPorDia(inicioDia: Long, finDia: Long): Flow<List<EventoEntity>>

    // Escrituras suspendidas: nunca en el hilo principal.
    @Insert
    suspend fun insertar(evento: EventoEntity): Long

    @Update
    suspend fun actualizar(evento: EventoEntity)

    @Delete
    suspend fun eliminar(evento: EventoEntity)

    @Query("SELECT * FROM eventos WHERE id = :id")
    suspend fun porId(id: Long): EventoEntity?

    @Query("UPDATE eventos SET hecho = 1 WHERE id = :id")
    suspend fun marcarHecho(id: Long)
}
