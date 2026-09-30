package com.example.petcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una mascota del usuario. Tabla `mascotas`.
 *
 * Fechas como `Long` (milisegundos, inicio del día local) para no depender
 * de `java.time` (minSdk 24). Ver [com.example.petcard.util.Dates].
 */
@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val especie: String,
    val raza: String,
    val fechaNacimientoMillis: Long,
    val fotoUri: String? = null,
    val pesoKg: Double? = null,
)
