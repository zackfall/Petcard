package com.example.petcard.ui.calendario

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcard.notifications.Notificaciones

/**
 * Contenedor CON estado del Calendario: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()` y traduce los eventos
 * puntuales en programar/cancelar alarmas. Pasa solo estado + lambdas.
 */
@Composable
fun CalendarioRoute(
    modifier: Modifier = Modifier,
) {
    val vm: CalendarioViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    LaunchedEffect(vm) {
        vm.eventos.collect { evento ->
            when (evento) {
                is CalendarioEvent.Programar ->
                    Notificaciones.programar(
                        contexto = contexto,
                        eventoId = evento.eventoId,
                        titulo = evento.titulo,
                        detalle = "Tienes un evento de salud hoy.",
                        triggerMillis = evento.triggerMillis,
                    )
                is CalendarioEvent.Cancelar ->
                    Notificaciones.cancelar(contexto, evento.eventoId)
            }
        }
    }

    CalendarioContent(
        estado = estado,
        onMesAnterior = vm::onMesAnterior,
        onMesSiguiente = vm::onMesSiguiente,
        onDiaElegido = vm::onDiaElegido,
        onAlternarRecordatorio = vm::onAlternarRecordatorio,
        modifier = modifier,
    )
}
