package com.example.petcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una mascota del usuario. Tabla `mascotas`.
 *
 * Fechas como `Long` (milisegundos, inicio del día local) para no depender
 * de `java.time` (minSdk 24). Ver [com.example.petcard.util.Dates].
 */
// MascotaEntity es la definición de la tabla mascotas: cada propiedad es una columna, el id se genera solo, y los campos con ? son opcionales.
//@Entity: le dice a Room “esta clase representa una tabla”.
//tableName = "mascotas": el nombre de la tabla. Esto responde la duda de antes: es exactamente el nombre que usaste en el SQL del DAO (FROM mascotas).
// Sin esto, Room usaría el nombre de la clase.
//data class: igual que los UiState, permite usar copy() y comparar por contenido.
@Entity(tableName = "mascotas")
data class MascotaEntity(

    // @PrimaryKey: marca la columna que identifica de forma única cada fila. Es lo que usan @Update y @Delete para saber cuál fila modificar o borrar.
    //autoGenerate = true: Room genera el id solo (1, 2, 3...). Tú no lo eliges.
    //= 0: valor por defecto. Al crear una mascota nueva no tienes id todavía, así que pones 0, y Room entiende “asígnale uno nuevo”.
    // Por eso en onGuardar() creaste MascotaEntity(nombre = ..., especie = ..., ...) sin pasar id. Ese id generado es el Long que devuelve insertar.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val especie: String,
    val raza: String,
    val fechaNacimientoMillis: Long,
    val fotoUri: String? = null,
    val pesoKg: Double? = null,
)
