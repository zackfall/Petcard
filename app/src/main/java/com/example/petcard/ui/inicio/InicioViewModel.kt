package com.example.petcard.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.EventoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    repositorio: PetRepository,
) : ViewModel() {

    private val formato = SimpleDateFormat("d MMM yyyy", Locale("es"))

    val estado: StateFlow<InicioUiState> = combine(
        repositorio.observarMascotas(),
        repositorio.observarEventos(),
    ) { mascotas, eventos ->
        val nombres = mascotas.associate { it.id to it.nombre }

        fun EventoEntity.aInicio() = EventoInicio(
            id = id,
            titulo = titulo,
            mascota = nombres[mascotaId] ?: "?",
            fecha = formato.format(Date(fechaMillis)),
        )

        InicioUiState(
            proximos = eventos.filter { !it.hecho }
                .sortedBy { it.fechaMillis }
                .take(3)
                .map { it.aInicio() },
            historial = eventos.filter { it.hecho }
                .sortedByDescending { it.fechaMillis }
                .take(5)
                .map { it.aInicio() },
            cargando = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())
}