package com.example.petcard.ui.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Privacidad de datos: crea su ViewModel con Hilt
 * y consume el estado con `collectAsStateWithLifecycle()`. Pasa solo estado
 * al Content (la pantalla no tiene acciones).
 */
@Composable
fun PrivacidadRoute(modifier: Modifier = Modifier) {
    val vm: PrivacidadViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()

    PrivacidadContent(
        estado = estado,
        modifier = modifier,
    )
}
