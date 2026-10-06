package com.example.petcard.ui.config

/** Estado único de la pantalla Privacidad de datos (rúbrica MVVM). */
data class PrivacidadUiState(
    val titulo: String = "Privacidad de datos",
    val encabezado: String = "Tus datos se guardan localmente",
    val descripcion: String =
        "PetCard usa Room para almacenar mascotas, eventos y salud " +
            "directamente en tu dispositivo.",
    val puntos: List<String> = listOf(
        "No requiere cuenta ni inicio de sesión.",
        "No se comparten con terceros.",
        "Podrían perderse al desinstalar la app o borrar sus datos.",
    ),
)
