package com.example.petcard.ui.eventos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petcard.data.PetRepository

/**
 * Contenedor CON estado de Eventos pendientes: crea su ViewModel con la
 * Factory y consume el estado con `collectAsStateWithLifecycle()`. Pasa solo
 * estado + lambdas al Content.
 */
@Composable
fun EventosPendientesRoute(
    repositorio: PetRepository,
    alCrearEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: EventosPendientesViewModel = viewModel(
        factory = EventosPendientesViewModel.factory(repositorio),
    )
    val estado by vm.uiState.collectAsStateWithLifecycle()

    EventosPendientesContent(
        estado = estado,
        onHecho = vm::onHecho,
        onEliminar = vm::onEliminar,
        onCrearEvento = alCrearEvento,
        modifier = modifier,
    )
}
