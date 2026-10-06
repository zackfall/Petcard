package com.example.petcard.ui.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Configuración: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()` y traduce el evento
 * puntual `NotificacionesPush` en activar/desactivar el canal del sistema.
 * Pasa solo estado + lambdas al Content.
 */
@Composable
fun ConfiguracionRoute(
    onPrivacidad: () -> Unit,
    onAcerca: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: ConfiguracionViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    LaunchedEffect(vm) {
        vm.inicializar(notificacionesPush = NotificacionesPush.activas(contexto))
        vm.eventos.collect { evento ->
            when (evento) {
                is ConfiguracionEvent.NotificacionesPush ->
                    NotificacionesPush.aplicar(contexto, evento.activas)
            }
        }
    }

    ConfiguracionContent(
        estado = estado,
        onModoOscuroChange = vm::onModoOscuroChange,
        onNotificacionesPushChange = vm::onNotificacionesPushChange,
        onPrivacidad = onPrivacidad,
        onAcerca = onAcerca,
        onCerrarSesion = { /* Pendiente: aún no hay usuarios en la app. */ },
        modifier = modifier,
    )
}
