package com.example.petcard.ui.mascotas

/**
 * Estado único de la pantalla Agregar Mascota (rúbrica MVVM: un solo StateFlow).
 *
 * El diseño pide "Edad (Años)" y [com.example.petcard.data.local.MascotaEntity]
 * guarda `fechaNacimientoMillis`: el ViewModel convierte la edad al nacimiento
 * antes de persistir. El peso del diseño (`pesoKg`) es opcional como en Room.
 */
data class AgregarMascotaUiState(
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edadAnos: String = "",
    val pesoKg: String = "",
    val fotoUri: String? = null,
    val guardando: Boolean = false,
    val error: String? = null,
) {
    val edadAnosInt: Int?
        get() = edadAnos.trim().toIntOrNull()?.takeIf { it in 0..200 }

    /** `null` si no hay peso o no es un número positivo; admite coma decimal. */
    val pesoKgDouble: Double?
        get() = pesoKg.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }

    val puedeGuardar: Boolean
        get() = nombre.isNotBlank() &&
            especie.isNotBlank() &&
            edadAnosInt != null &&
            !guardando
}
