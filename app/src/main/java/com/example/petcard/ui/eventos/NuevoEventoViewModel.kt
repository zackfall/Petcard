package com.example.petcard.ui.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.data.local.TipoEvento
import com.example.petcard.util.Dates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estado único de la pantalla Nuevo Evento (rúbrica MVVM: un solo StateFlow). */
data class NuevoEventoUiState(
    val mascotas: List<MascotaEntity> = emptyList(),
    val mascotaId: Long? = null,
    val titulo: String = "",
    /** Null = "Ninguno" (el diseño obliga a elegirlo explícitamente). */
    val tipo: TipoEvento? = null,
    val fechaMillis: Long = Dates.hoyInicioMillis(),
    val horaMinutos: Int? = null,
    val lugar: String = "",
    val notas: String = "",
    /** Próxima fecha del recordatorio (inicio del día). Null = sin aviso. */
    val proximaMillis: Long? = null,
    val guardando: Boolean = false,
) {
    val puedeGuardar: Boolean
        get() = mascotaId != null && titulo.isNotBlank() && tipo != null && !guardando
}

/** Eventos puntuales (one-off): no van en el StateFlow. */
sealed interface NuevoEventoEvent {
    /** El recordatorio solo se programa si [triggerMillis] no es null. */
    data class Guardado(
        val eventoId: Long,
        val titulo: String,
        val mascotaNombre: String,
        val triggerMillis: Long?,
    ) : NuevoEventoEvent
}

@HiltViewModel
class NuevoEventoViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {
    private val formulario = MutableStateFlow(NuevoEventoUiState())

    val uiState: StateFlow<NuevoEventoUiState> =
        combine(formulario, repositorio.observarMascotas()) { form, mascotas ->
            form.copy(
                mascotas = mascotas,
                // Si la mascota elegida desaparece, limpiar la selección.
                mascotaId = form.mascotaId?.takeIf { id -> mascotas.any { it.id == id } },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NuevoEventoUiState())

    private val _eventos = Channel<NuevoEventoEvent>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    fun onMascotaChange(id: Long?) {
        formulario.value = formulario.value.copy(mascotaId = id)
    }

    fun onTituloChange(titulo: String) {
        formulario.value = formulario.value.copy(titulo = titulo)
    }

    fun onTipoChange(tipo: TipoEvento) {
        formulario.value = formulario.value.copy(tipo = tipo)
    }

    fun onFechaChange(fechaMillis: Long) {
        formulario.value = formulario.value.copy(fechaMillis = fechaMillis)
    }

    fun onHoraChange(horaMinutos: Int?) {
        formulario.value = formulario.value.copy(horaMinutos = horaMinutos)
    }

    fun onLugarChange(lugar: String) {
        formulario.value = formulario.value.copy(lugar = lugar)
    }

    fun onRecordatorioChange(proximaMillis: Long?) {
        formulario.value = formulario.value.copy(proximaMillis = proximaMillis)
    }

    fun onNotasChange(notas: String) {
        formulario.value = formulario.value.copy(notas = notas)
    }

    fun onGuardar() {
        val actual = formulario.value
        if (!actual.puedeGuardar) return
        formulario.value = actual.copy(guardando = true)
        viewModelScope.launch {
            // El aviso suena el día de la próxima fecha, a la hora del evento
            // (o a las 09:00 si no indicó hora).
            val trigger = actual.proximaMillis?.plus(
                ((actual.horaMinutos ?: 9 * 60) * 60_000L),
            )
            val id = repositorio.guardarEvento(
                EventoEntity(
                    mascotaId = actual.mascotaId!!,
                    tipo = actual.tipo!!,
                    titulo = actual.titulo.trim(),
                    fechaMillis = actual.fechaMillis,
                    horaMinutos = actual.horaMinutos,
                    lugar = actual.lugar.trim().takeIf { it.isNotBlank() },
                    notas = actual.notas.trim().takeIf { it.isNotBlank() },
                    proximaMillis = actual.proximaMillis,
                ),
            )
            val nombre = actual.mascotas.firstOrNull { it.id == actual.mascotaId }?.nombre ?: ""
            _eventos.send(NuevoEventoEvent.Guardado(id, actual.titulo.trim(), nombre, trigger))
        }
    }
}
