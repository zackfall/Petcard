package com.example.petcard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un evento de salud asociado a una mascota. Tabla `eventos`.
 *
 * - [fechaMillis]: inicio del día local en milisegundos.
 * - [horaMinutos]: minutos desde medianoche (ej. 600 = 10:00). Null = sin hora.
 * - [lugar]: clínica / veterinaria. Null = no indicado.
 * - [proximaMillis]: fecha del próximo recordatorio. Null = sin recordatorio.
 * - [hecho]: true cuando el usuario lo marca como realizado (botón "Hecho").
 */
@Entity(
    tableName = "eventos",
    foreignKeys = [
        ForeignKey(
            entity = MascotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("mascotaId"), Index("fechaMillis")],
)
data class EventoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mascotaId: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val fechaMillis: Long,
    val horaMinutos: Int? = null,
    val lugar: String? = null,
    val notas: String? = null,
    val proximaMillis: Long? = null,
    val hecho: Boolean = false,
)
