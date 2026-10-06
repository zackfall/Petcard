package com.example.petcard.ui.config

/** Estado único de la pantalla Configuración (rúbrica MVVM). */
data class ConfiguracionUiState(
    /** Por ahora solo se refleja en el switch: el tema aún no se cambia. */
    val modoOscuro: Boolean = false,
    /** Estado real del canal de recordatorios (leído del sistema). */
    val notificacionesPush: Boolean = true,
)
