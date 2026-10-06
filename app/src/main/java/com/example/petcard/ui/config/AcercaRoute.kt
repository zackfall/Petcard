package com.example.petcard.ui.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Acerca de PetCard: crea su ViewModel con Hilt y
 * consume el estado con `collectAsStateWithLifecycle()`. Pasa solo estado al
 * Content (la pantalla no tiene acciones).
 */
@Composable
fun AcercaRoute(modifier: Modifier = Modifier) {
    val vm: AcercaViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()

    AcercaContent(
        estado = estado,
        modifier = modifier,
    )
}
