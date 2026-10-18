package com.example.petcard.ui.mascotas

/**
 * Estado único de la pantalla Agregar Mascota (rúbrica MVVM: un solo StateFlow).
 *
 * El diseño pide "Edad (Años)" y [com.example.petcard.data.local.MascotaEntity]
 * guarda `fechaNacimientoMillis`: el ViewModel convierte la edad al nacimiento
 * antes de persistir. El peso del diseño (`pesoKg`) es opcional como en Room.
 */

// AgregarMascotaUiState es el paquete de datos del formulario: guarda lo que el usuario escribe (todo como texto), si se está guardando y si hay un error,
//  y calcula la edad y el peso como números válidos y si el botón de guardar debe estar habilitado.
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

    //Convierte el texto de la edad en un número válido
    //.trim(): quita espacios al inicio y al final.
    //.toIntOrNull(): intenta convertir a Int. Si no se puede (vacío, o letras), devuelve null en vez de dar error.
    //.takeIf { it in 0..200 }: solo acepta el número si está entre 0 y 20. Si no, devuelve null.
    val edadAnosInt: Int?
        get() = edadAnos.trim().toIntOrNull()?.takeIf { it in 0..20 }

    /** `null` si no hay peso o no es un número positivo; admite coma decimal. */
    //.replace(',', '.'): cambia la coma por punto. En español se escribe “32,5” pero Kotlin solo entiende “32.5”.
    //.toDoubleOrNull(): convierte a número decimal, o null si no se puede.
    //.takeIf { it > 0 }: solo acepta pesos positivos. El 0 y los negativos dan null.
    val pesoKgDouble: Double?
        get() = pesoKg.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }


    //Es la que controla el botón “Listo ✓” (enabled = estado.puedeGuardar) y la protección de onGuardar(). Solo es true si se cumplen las cuatro condiciones a la vez
    val puedeGuardar: Boolean
        get() = nombre.isNotBlank() &&
            especie.isNotBlank() &&
            edadAnosInt != null &&
            !guardando
}
