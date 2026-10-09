package com.example.petcard.ui.mascotas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Perfil de una mascota concreta: su `mascotaId` llega de la ruta
 * `perfil_mascota/{mascotaId}` vía [SavedStateHandle] (lo rellena NavHost).
 * Lee la mascota y su historial con Flow del repositorio (Room = fuente única).
 */
@HiltViewModel
class PerfilMascotaViewModel @Inject constructor(
    private val repositorio: PetRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val mascotaId: Long = savedStateHandle[Routes.ARG_MASCOTA_ID] ?: -1L

    val uiState: StateFlow<PerfilMascotaUiState> = combine(
        repositorio.observarMascota(mascotaId),
        repositorio.observarEventosDeMascota(mascotaId),
    ) { mascota, eventos ->
        PerfilMascotaUiState(
            cargando = false,
            mascota = mascota,
            eventos = eventos.sortedByDescending { it.fechaMillis },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilMascotaUiState())
}
