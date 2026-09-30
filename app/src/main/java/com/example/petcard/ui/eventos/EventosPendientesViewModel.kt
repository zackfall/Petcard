package com.example.petcard.ui.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.petcard.data.PetRepository
import com.example.petcard.ui.shared.EventoItem
import com.example.petcard.ui.shared.aItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado único de la pantalla Eventos pendientes. */
data class PendientesUiState(
    val eventos: List<EventoItem> = emptyList(),
    val cargando: Boolean = true,
)

class EventosPendientesViewModel(
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

    companion object {
        /** DI manual: el ViewModel jamás instancia el repositorio. */
        fun factory(repositorio: PetRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { EventosPendientesViewModel(repositorio) }
            }
    }
}
