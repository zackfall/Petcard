package com.example.petcard.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Eventos puntuales (one-off): la Route los aplica sobre el sistema. */
sealed interface ConfiguracionEvent {
    /** Activar o desactivar los avisos de PetCard. */
    data class NotificacionesPush(val activas: Boolean) : ConfiguracionEvent
}

@HiltViewModel
class ConfiguracionViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<ConfiguracionEvent>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    /**
     * Refleja el estado real de las notificaciones (la Route lo lee del canal).
     * No toca el modo oscuro, que vive solo en la UI hasta que se implemente.
     */
    fun inicializar(notificacionesPush: Boolean) {
        _uiState.update { it.copy(notificacionesPush = notificacionesPush) }
    }

    /** Tema claro/oscuro: pendiente, por ahora solo mueve el switch. */
    fun onModoOscuroChange(activado: Boolean) {
        _uiState.update { it.copy(modoOscuro = activado) }
    }

    fun onNotificacionesPushChange(activas: Boolean) {
        _uiState.update { it.copy(notificacionesPush = activas) }
        viewModelScope.launch {
            _eventos.send(ConfiguracionEvent.NotificacionesPush(activas))
        }
    }
}
