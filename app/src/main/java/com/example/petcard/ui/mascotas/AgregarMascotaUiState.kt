package com.example.petcard.ui.mascotas

/**
 * Estado único de la pantalla Agregar Mascota (rúbrica MVVM: un solo StateFlow).
 *
 * El diseño pide "Edad (Años)" y [com.example.petcard.data.local.MascotaEntity]
 * guarda `fechaNacimientoMillis`: el ViewModel convierte la edad al nacimiento
 * antes de persistir.
 */
data class AgregarMascotaUiState(
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edadAnos: String = "",
    val fotoUri: String? = null,
    val guardando: Boolean = false,
) {
    val edadAnosInt: Int?
        get() = edadAnos.trim().toIntOrNull()?.takeIf { it in 0..200 }

    val puedeGuardar: Boolean
        get() = nombre.isNotBlank() &&
            especie.isNotBlank() &&
            edadAnosInt != null &&
            !guardando
}
