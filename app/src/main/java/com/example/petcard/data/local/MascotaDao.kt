package com.example.petcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {
    // Lecturas reactivas: la UI se actualiza sola cuando cambia la BD.
    @Query("SELECT * FROM mascotas ORDER BY nombre ASC")
    fun observarTodas(): Flow<List<MascotaEntity>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarPorId(id: Long): Flow<MascotaEntity?>

    // Escrituras suspendidas: nunca en el hilo principal.
    @Insert
    suspend fun insertar(mascota: MascotaEntity): Long

    @Update
    suspend fun actualizar(mascota: MascotaEntity)

    @Delete
    suspend fun eliminar(mascota: MascotaEntity)
}
