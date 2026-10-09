package com.example.petcard.ui.mascotas

import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaEntity

/**
 * Estado único de la pantalla Perfil de Mascota (rúbrica MVVM: un solo StateFlow).
 * La lista de eventos llega ordenada del ViewModel (más reciente primero).
 */
data class PerfilMascotaUiState(
    val cargando: Boolean = true,
    val mascota: MascotaEntity? = null,
    val eventos: List<EventoEntity> = emptyList(),
) {
    val encontrada: Boolean get() = mascota != null

    /** "Golden Retriever • 3 años" (diseño screen-container-2). */
    val subtitulo: String get() = mascota?.lineaPerfil().orEmpty()
}
