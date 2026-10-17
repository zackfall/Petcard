package com.example.petcard.ui.mascotas

import com.example.petcard.data.local.MascotaEntity

// Es la pieza que une el ViewModel con MascotasContent: el ViewModel la produce y Content la consume.
//MascotasUiState es el “paquete de datos” de la pantalla de lista: guarda las mascotas a mostrar y si todavía se están cargando, con valores iniciales que significan
// “aún no hay nada, estoy esperando a la base de datos”.
data class MascotasUiState(
    // mascotas: la lista de mascotas que se va a dibujar. Por defecto es una lista vacía (emptyList()).
    val mascotas: List<MascotaEntity> = emptyList(),
    // cargando: indica si los datos todavía están llegando de la base de datos. Por defecto es true.
    val cargando: Boolean = true,
)