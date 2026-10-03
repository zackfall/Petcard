package com.example.petcard.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MascotasViewModel(private val repositorio: PetRepository) : ViewModel() {

    val estado: StateFlow<MascotasUiState> = repositorio.observarMascotas()
        .map { MascotasUiState(mascotas = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MascotasUiState())

    fun eliminar(mascota: MascotaEntity) {
        viewModelScope.launch { repositorio.eliminarMascota(mascota) }
    }

    companion object {
        fun factory(repositorio: PetRepository) = viewModelFactory {
            initializer { MascotasViewModel(repositorio) }
        }
    }
}