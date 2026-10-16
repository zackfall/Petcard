package com.example.petcard.ui.config

/** Estado único de la pantalla Configuración (rúbrica MVVM). */
data class ConfiguracionUiState(
    /** Tema elegido (persistido en [com.example.petcard.data.AjustesRepository]). */
    val modoOscuro: Boolean = false,
    /** Estado real del canal de recordatorios (leído del sistema). */
    val notificacionesPush: Boolean = true,
)
