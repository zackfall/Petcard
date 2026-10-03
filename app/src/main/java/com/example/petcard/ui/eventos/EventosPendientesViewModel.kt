package com.example.petcard.ui.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.ui.shared.EventoItem
import com.example.petcard.ui.shared.aItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estado único de la pantalla Eventos pendientes. */
data class PendientesUiState(
    val eventos: List<EventoItem> = emptyList(),
    val cargando: Boolean = true,
)

@HiltViewModel
class EventosPendientesViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {
    val uiState: StateFlow<PendientesUiState> = combine(
        repositorio.observarPendientes(),
        repositorio.observarMascotas(),
    ) { pendientes, mascotas ->
        val nombres = mascotas.associate { it.id to it.nombre }
        PendientesUiState(
            eventos = pendientes.map { it.aItem(nombres[it.mascotaId] ?: "?") },
            cargando = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PendientesUiState())

    /** Botón "Hecho": el evento sale de la lista solo, vía el Flow reactivo. */
    fun onHecho(id: Long) {
        viewModelScope.launch {
            repositorio.marcarEventoHecho(id)
        }
    }

    /** Papelera del diseño: elimina el evento definitivamente. */
    fun onEliminar(id: Long) {
        viewModelScope.launch {
            repositorio.obtenerEvento(id)?.let { repositorio.eliminarEvento(it) }
        }
    }
}
