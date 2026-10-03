package com.example.petcard.ui.eventos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Eventos pendientes: crea su ViewModel con
 * Hilt y consume el estado con `collectAsStateWithLifecycle()`. Pasa solo
 * estado + lambdas al Content.
 */
@Composable
fun EventosPendientesRoute(
    alCrearEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: EventosPendientesViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()

    EventosPendientesContent(
        estado = estado,
        onHecho = vm::onHecho,
        onEliminar = vm::onEliminar,
        onCrearEvento = alCrearEvento,
        modifier = modifier,
    )
}
