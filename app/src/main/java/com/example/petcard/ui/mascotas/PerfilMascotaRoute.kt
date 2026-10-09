package com.example.petcard.ui.mascotas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Perfil de Mascota: crea su ViewModel con Hilt (el
 * `mascotaId` de la ruta llega solo, vía SavedStateHandle) y consume el estado
 * con `collectAsStateWithLifecycle()`. Pasa solo estado + lambdas al Content.
 */
@Composable
fun PerfilMascotaRoute(
    alNuevoEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: PerfilMascotaViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()

    PerfilMascotaContent(
        estado = estado,
        onNuevoEvento = alNuevoEvento,
        modifier = modifier,
    )
}
