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

// PerfilMascotaRoute obtiene el PerfilMascotaViewModel, observa su estado y se lo pasa a PerfilMascotaContent, dejando la navegación a alNuevoEvento,
// porque esta pantalla solo muestra datos y no modifica nada.
@Composable
fun PerfilMascotaRoute(
    alNuevoEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    //Pide a Hilt el PerfilMascotaViewModel, con sus dependencias ya inyectadas (como el PetRepository).
    val vm: PerfilMascotaViewModel = hiltViewModel()

    //Observa el uiState del ViewModel. Cada vez que cambia (termina de cargar, llega la mascota, se agrega un evento),
    // la pantalla se redibuja. Se detiene sola cuando la app pasa a segundo plano.
    val estado by vm.uiState.collectAsStateWithLifecycle()

    //Le da a Content el estado para dibujar y la función para cuando el usuario toque “+ Nuevo Evento”.
    PerfilMascotaContent(
        estado = estado,
        onNuevoEvento = alNuevoEvento,
        modifier = modifier,
    )
}
