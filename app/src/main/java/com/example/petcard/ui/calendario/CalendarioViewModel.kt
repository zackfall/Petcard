package com.example.petcard.ui.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.petcard.data.PetRepository
import com.example.petcard.ui.shared.EventoItem
import com.example.petcard.ui.shared.aItem
import com.example.petcard.util.Dates
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/** Una celda de la grilla mensual. `numero == null` = hueco vacío. */
data class CeldaDia(
    val numero: Int?,
    val millis: Long?,
    val tieneEventos: Boolean = false,
    val esHoy: Boolean = false,
    val seleccionado: Boolean = false,
)

/** Estado único de la pantalla Calendario. */
data class CalendarioUiState(
    val anio: Int = 2026,
    val mes: Int = 10,
    val celdas: List<CeldaDia> = emptyList(),
    val diaSeleccionado: Long = Dates.hoyInicioMillis(),
    val eventos: List<EventoItem> = emptyList(),
    val cargando: Boolean = true,
)

class CalendarioViewModel(
    private val repositorio: PetRepository,
) : ViewModel() {
    private val hoy = Dates.hoyInicioMillis()
    private val calInicial = Calendar.getInstance()

    private val mesElegido = MutableStateFlow(
        calInicial.get(Calendar.YEAR) to (calInicial.get(Calendar.MONTH) + 1),
    )
    private val diaElegido = MutableStateFlow(hoy)

    private val _eventos = Channel<CalendarioEvent>(Channel.BUFFERED)
    /** One-off: programar o cancelar la alarma del sistema. Lo ejecuta la Route. */
    val eventos = _eventos.receiveAsFlow()

    val uiState: StateFlow<CalendarioUiState> = combine(
        mesElegido,
        diaElegido,
        repositorio.observarEventos(),
        repositorio.observarMascotas(),
    ) { (anio, mes), dia, eventos, mascotas ->
        val nombres = mascotas.associate { it.id to it.nombre }
        val diasConEvento = eventos
            .filter { !it.hecho }
            .map { it.fechaMillis }
            .toSet()
        CalendarioUiState(
            anio = anio,
            mes = mes,
            celdas = construirCeldas(anio, mes, dia, diasConEvento),
            diaSeleccionado = dia,
            eventos = eventos
                .filter { it.fechaMillis == dia }
                .map { it.aItem(nombres[it.mascotaId] ?: "?") },
            cargando = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarioUiState())

    fun onMesAnterior() {
        val (anio, mes) = mesElegido.value
        mesElegido.value = if (mes == 1) (anio - 1) to 12 else anio to (mes - 1)
    }

    fun onMesSiguiente() {
        val (anio, mes) = mesElegido.value
        mesElegido.value = if (mes == 12) (anio + 1) to 1 else anio to (mes + 1)
    }

    fun onDiaElegido(millis: Long) {
        diaElegido.value = millis
    }

    /**
     * Campana del diseño: si el evento no tiene aviso, se lo pone para su
     * propia fecha/hora; si ya lo tiene, lo quita (y cancela la alarma).
     */
    fun onAlternarRecordatorio(eventoId: Long) {
        viewModelScope.launch {
            val entidad = repositorio.obtenerEvento(eventoId) ?: return@launch
            if (entidad.proximaMillis == null) {
                val proxima = entidad.fechaMillis
                repositorio.actualizarEvento(entidad.copy(proximaMillis = proxima))
                val trigger = proxima + ((entidad.horaMinutos ?: 9 * 60) * 60_000L)
                if (trigger > System.currentTimeMillis()) {
                    _eventos.send(
                        CalendarioEvent.Programar(
                            eventoId = eventoId,
                            titulo = entidad.titulo,
                            triggerMillis = trigger,
                        ),
                    )
                }
            } else {
                repositorio.actualizarEvento(entidad.copy(proximaMillis = null))
                _eventos.send(CalendarioEvent.Cancelar(eventoId))
            }
        }
    }

    private fun construirCeldas(
        anio: Int,
        mes: Int,
        diaSeleccionado: Long,
        diasConEvento: Set<Long>,
    ): List<CeldaDia> {
        val cal = Calendar.getInstance()
        cal.set(anio, mes - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // La grilla empieza en domingo (Do Lu Ma Mi Ju Vi Sá).
        val huecos = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7
        val diasEnMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val celdas = mutableListOf<CeldaDia>()
        repeat(huecos) { celdas += CeldaDia(null, null) }
        for (dia in 1..diasEnMes) {
            val millis = Dates.inicioDiaMillis(anio, mes, dia)
            celdas += CeldaDia(
                numero = dia,
                millis = millis,
                tieneEventos = millis in diasConEvento,
                esHoy = millis == hoy,
                seleccionado = millis == diaSeleccionado,
            )
        }
        return celdas
    }

    companion object {
        /** DI manual: el ViewModel jamás instancia el repositorio. */
        fun factory(repositorio: PetRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { CalendarioViewModel(repositorio) }
            }
    }
}

/** Eventos puntuales del calendario (alarmas del sistema). */
sealed interface CalendarioEvent {
    data class Programar(
        val eventoId: Long,
        val titulo: String,
        val triggerMillis: Long,
    ) : CalendarioEvent

    data class Cancelar(val eventoId: Long) : CalendarioEvent
}
