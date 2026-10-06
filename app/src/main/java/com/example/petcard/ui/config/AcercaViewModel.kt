package com.example.petcard.ui.config

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Estado fijo de Acerca de PetCard (información del README): el texto vive
 * en el UiState y el Content solo lo pinta (un único StateFlow por pantalla).
 */
@HiltViewModel
class AcercaViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AcercaUiState())
    val uiState: StateFlow<AcercaUiState> = _uiState.asStateFlow()
}
