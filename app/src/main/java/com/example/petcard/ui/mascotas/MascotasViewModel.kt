package com.example.petcard.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MascotasViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {

    val estado: StateFlow<MascotasUiState> = repositorio.observarMascotas()
        .map { MascotasUiState(mascotas = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MascotasUiState())

    fun eliminar(mascota: MascotaEntity) {
        viewModelScope.launch { repositorio.eliminarMascota(mascota) }
    }
}