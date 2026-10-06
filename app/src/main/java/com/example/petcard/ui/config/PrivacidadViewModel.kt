package com.example.petcard.ui.config

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Estado fijo de Privacidad de datos: el texto del diseño vive en el
 * UiState y el Content solo lo pinta (un único StateFlow por pantalla).
 */
@HiltViewModel
class PrivacidadViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacidadUiState())
    val uiState: StateFlow<PrivacidadUiState> = _uiState.asStateFlow()
}
