package com.example.petcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// @Dao le dice a Room: “esta interfaz define cómo acceder a la tabla de mascotas”.
// MascotaDao es la interfaz donde declaras qué consultas y operaciones se pueden hacer sobre la tabla mascotas: las lecturas devuelven Flow que se actualizan solos,
// y las escrituras (@Insert, @Update, @Delete) son suspend y Room genera el código por ti.
@Dao
interface MascotaDao {
    // Lecturas reactivas: la UI se actualiza sola cuando cambia la BD.
    //SELECT * FROM mascotas: “trae todas las columnas de todas las filas de la tabla mascotas”.
    //ORDER BY nombre ASC: ordénalas por nombre, de la A a la Z. Por eso tu lista de mascotas aparece en orden alfabético sin que lo hagas en Kotlin.
    //Flow<List<MascotaEntity>>: devuelve un flujo reactivo. Room vuelve a ejecutar la consulta y emite la lista nueva cada vez que la tabla cambia.
    // Esto es lo que hace que MascotasViewModel se actualice solo al eliminar una mascota.
    @Query("SELECT * FROM mascotas ORDER BY nombre ASC")
    fun observarTodas(): Flow<List<MascotaEntity>>

    // WHERE id = :id: filtra por la mascota con ese id. El :id con dos puntos se reemplaza por el parámetro id de la función.
    //MascotaEntity?: con ? porque puede no existir (por ejemplo, si se borró). En ese caso el flujo emite null.
    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarPorId(id: Long): Flow<MascotaEntity?>

    // Escrituras suspendidas: nunca en el hilo principal.
    // @Insert	Agrega una fila nueva	    El id generado (Long)

    @Insert
    suspend fun insertar(mascota: MascotaEntity): Long

    //@Update	Modifica la fila que tenga el mismo id	    Nada
    @Update
    suspend fun actualizar(mascota: MascotaEntity)

    //@Delete	Borra la fila que tenga el mismo id	    Nada
    @Delete
    suspend fun eliminar(mascota: MascotaEntity)
}
