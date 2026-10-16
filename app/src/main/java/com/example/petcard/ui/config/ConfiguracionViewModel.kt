package com.example.petcard.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.AjustesRepository
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
class ConfiguracionViewModel @Inject constructor(
    private val ajustes: AjustesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<ConfiguracionEvent>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    /**
     * Refleja el estado real de ambos ajustes. El tema persistido manda;
     * si el usuario aún no eligió, se usa el modo del sistema [modoOscuroSistema]
     * (la Route lo lee con `isSystemInDarkTheme()`).
     */
    fun inicializar(notificacionesPush: Boolean, modoOscuroSistema: Boolean) {
        _uiState.update {
            it.copy(
                notificacionesPush = notificacionesPush,
                modoOscuro = ajustes.modoOscuro.value ?: modoOscuroSistema,
            )
        }
    }

    fun onModoOscuroChange(activado: Boolean) {
        _uiState.update { it.copy(modoOscuro = activado) }
        viewModelScope.launch {
            ajustes.guardarModoOscuro(activado)
        }
    }

    fun onNotificacionesPushChange(activas: Boolean) {
        _uiState.update { it.copy(notificacionesPush = activas) }
        viewModelScope.launch {
            _eventos.send(ConfiguracionEvent.NotificacionesPush(activas))
        }
    }
}
