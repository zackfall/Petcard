package com.example.petcard.ui.config

/** Dato etiqueta/valor del bloque "Contexto académico". */
data class DatoAcerca(val etiqueta: String, val valor: String)

/** Estado único de la pantalla Acerca de PetCard (rúbrica MVVM). */
data class AcercaUiState(
    val titulo: String = "Acerca de PetCard",
    val descripcion: String =
        "PetCard es una aplicación móvil que funciona como un carné de vacunación " +
            "y control de salud digital para mascotas, permitiendo al dueño " +
            "registrar y consultar de forma rápida y sin conexión a internet el " +
            "historial de vacunas, desparasitaciones y controles veterinarios de " +
            "su mascota.",
    val contexto: List<DatoAcerca> = listOf(
        DatoAcerca("Asignatura", "Aplicaciones Móviles"),
        DatoAcerca("Universidad", "Universidad Laica Eloy Alfaro de Manabí"),
        DatoAcerca("Facultad", "Ciencias Informáticas"),
        DatoAcerca("Carrera", "Tecnología de la Información"),
        DatoAcerca("Docente", "Joffre Edgardo Panchana Flores"),
    ),
    val integrantes: List<String> = listOf(
        "Eduardo Josue López López",
        "Castillo Loor José Manuel",
        "Reyes Peñaherrera Pierina Natalia",
        "Ruben Isaac Zamora Reyes",
    ),
    val caracteristicas: List<String> = listOf(
        "100% offline-first: la información se guarda en tu dispositivo.",
        "Un solo usuario: sin cuentas, roles ni invitaciones.",
        "Funciona sin internet ni servidores externos.",
    ),
    val version: String = "Versión 1.0",
)
