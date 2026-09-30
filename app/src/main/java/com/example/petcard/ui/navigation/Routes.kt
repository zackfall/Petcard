package com.example.petcard.ui.navigation

/**
 * Rutas centralizadas de navegación. CONGELADAS: ningún integrante las
 * renombra sin acuerdo del equipo (dueño del shell).
 *
 * Mapa diseño -> ruta:
 * - screen-container.pdf   -> INICIO (dashboard Salud)
 * - screen-container-1.pdf -> MASCOTAS
 * - screen-container-2.pdf -> PERFIL_MASCOTA (argumento mascotaId)
 * - screen-container-3.pdf -> AGREGAR_MASCOTA
 * - screen-container-4.pdf -> NUEVO_EVENTO
 * - screen-container-5.pdf -> CALENDARIO
 * - screen-container-6.pdf -> EVENTOS_PENDIENTES
 * - screen-container-7.pdf -> CONFIGURACION
 * - screen-container-8.pdf -> PRIVACIDAD
 */
object Routes {
    const val INICIO = "inicio"
    const val MASCOTAS = "mascotas"
    const val PERFIL_MASCOTA = "perfil_mascota/{mascotaId}"
    const val AGREGAR_MASCOTA = "agregar_mascota"
    const val NUEVO_EVENTO = "nuevo_evento"
    const val CALENDARIO = "calendario"
    const val EVENTOS_PENDIENTES = "eventos_pendientes"
    const val CONFIGURACION = "configuracion"
    const val PRIVACIDAD = "privacidad"

    const val ARG_MASCOTA_ID = "mascotaId"

    fun perfilMascota(mascotaId: Long): String = "perfil_mascota/$mascotaId"
}
