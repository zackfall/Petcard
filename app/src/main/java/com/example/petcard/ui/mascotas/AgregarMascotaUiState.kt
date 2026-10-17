package com.example.petcard.ui.mascotas

/**
 * Estado único de la pantalla Agregar Mascota (rúbrica MVVM: un solo StateFlow).
 *
 * El diseño pide "Edad (Años)" y [com.example.petcard.data.local.MascotaEntity]
 * guarda `fechaNacimientoMillis`: el ViewModel convierte la edad al nacimiento
 * antes de persistir.
 */

// Es la foto completa de lo que hay que dibujar en pantalla en un momento dado.
// No es un formulario, no es la base de datos: es el puente entre el ViewModel y el Content.
//El VM expone 1 StateFlow<AgregarMascotaUiState>.
//La UI lee 1 cosa y pinta todo eso.
//Nunca puede quedar la pantalla "a medio actualizar"
data class AgregarMascotaUiState( //// estado inicial: todo vacío, botón deshabilitado
    val nombre: String = "", //el usuario escribe texto libre; arrancan vacíos para que el OutlinedTextField no truene
    val especie: String = "",
    val raza: String = "",
    val edadAnos: String = "", //esto es importante: mientras el usuario escribe, el teclado deja pasar basura, guardarlo como String evita el crash
    val fotoUri: String? = null, //el signo ? = "puede no haber foto". null = el usuario todavía no eligió. El diseño justamente muestra un placeholder cuando no hay foto
    val guardando: Boolean = false, //flag de "estoy guardando"; sirve para deshabilitar el botón y cambiar el texto a "Guardando..."
) {

    // Un getter es un método de programación que sirve para obtener o recuperar el valor de un atributo privado o protegido de una clase
    val edadAnosInt: Int?
        get() = edadAnos.trim().toIntOrNull()?.takeIf { it in 0..20 } //trim()= quita los espacios de los numeros
                                                                            //toIntOrNull()= convierte a numero si puede lo devuelve como numero y si no como null
                                                                            //?.= si no es null sigue, pero si ya dio un null el resultado entero sera null y no se ejecutara el resto
                                                                            // takeIf { it in 0..20 }= es im filtro de cordura si alguien escribe 9999 lo descarta y devuelve null

    // Es una validación declarativa: describes qué condiciones hacen que el botón esté habilitado, sin escribir un if en ninguna parte de la UI.
    val puedeGuardar: Boolean
        get() = nombre.isNotBlank() &&
            especie.isNotBlank() &&
            edadAnosInt != null &&
            !guardando
}
