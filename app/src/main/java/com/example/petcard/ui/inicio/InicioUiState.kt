package com.example.petcard.ui.inicio

data class EventoInicio(
    val id: Long,
    val titulo: String,
    val mascota: String,
    val fecha: String,
)

data class InicioUiState(
    val proximos: List<EventoInicio> = emptyList(),
    val historial: List<EventoInicio> = emptyList(),
    val cargando: Boolean = true,
)