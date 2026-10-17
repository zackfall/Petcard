package com.example.petcard.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Preferencias de la app (tema), persistidas en disco.
 *
 * REGLA DE EQUIPO: misma forma que [PetRepository] — interfaz en `data/`,
 * implementación enlazada en `di/RepositoryModule`, inyectada a los
 * ViewModels con Hilt. El ViewModel jamás toca `Context`.
 */
interface AjustesRepository {

    /**
     * `true`/`false` = elección guardada del usuario; `null` = aún no eligió
     * y la app sigue el modo del sistema.
     */
    val modoOscuro: StateFlow<Boolean?>

    suspend fun guardarModoOscuro(valor: Boolean)
}
