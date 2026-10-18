package com.example.petcard.ui.mascotas

import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaEntity

/**
 * Estado único de la pantalla Perfil de Mascota (rúbrica MVVM: un solo StateFlow).
 * La lista de eventos llega ordenada del ViewModel (más reciente primero).
 */

//PerfilMascotaUiState guarda si está cargando, la mascota y sus eventos, y calcula encontrada (si la mascota existe) y subtitulo (el texto “Raza • edad”),
// de modo que Content solo tenga que leer valores ya listos.
data class PerfilMascotaUiState(
    // cargando: por defecto true, porque al abrir la pantalla los datos aún no llegan de la base de datos.
    //mascota: la mascota a mostrar. Es MascotaEntity? (puede ser null), porque al inicio todavía no se tiene, o porque puede no existir.
    //eventos: su historial, vacío al inicio.
    val cargando: Boolean = true,
    val mascota: MascotaEntity? = null,
    val eventos: List<EventoEntity> = emptyList(),
) {
    //Es true si hay una mascota y false si es null.
    val encontrada: Boolean get() = mascota != null

    /** "Golden Retriever • 3 años" (diseño screen-container-2). */
    // usa la función de extensión lineaPerfil() del archivo de utilidades, que produce “Golden Retriever • 3 años”. Por partes:
    //
    //mascota?.lineaPerfil(): el ?. llama a la función solo si mascota no es null; si es null, devuelve null.
    //.orEmpty(): convierte ese posible null en un texto vacío "". Así subtitulo siempre es un String normal y nunca String?.
    val subtitulo: String get() = mascota?.lineaPerfil().orEmpty()
}
