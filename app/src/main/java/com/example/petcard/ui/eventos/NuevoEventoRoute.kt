package com.example.petcard.ui.eventos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcard.notifications.Notificaciones

/**
 * Contenedor CON estado de Nuevo Evento: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()` y traduce el evento
 * puntual `Guardado` en (1) programar el aviso y (2) navegar atrás.
 * Pasa solo estado + lambdas al Content.
 */
@Composable
fun NuevoEventoRoute(
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: NuevoEventoViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    LaunchedEffect(vm) {
        vm.eventos.collect { evento ->
            if (evento is NuevoEventoEvent.Guardado) {
                evento.triggerMillis?.let { trigger ->
                    if (trigger > System.currentTimeMillis()) {
                        Notificaciones.programar(
                            contexto = contexto,
                            eventoId = evento.eventoId,
                            titulo = evento.titulo,
                            detalle = "${evento.mascotaNombre} • Próximo control",
                            triggerMillis = trigger,
                        )
                    }
                }
                alGuardar()
            }
        }
    }

    NuevoEventoContent(
        estado = estado,
        onMascotaChange = vm::onMascotaChange,
        onTituloChange = vm::onTituloChange,
        onTipoChange = vm::onTipoChange,
        onFechaChange = vm::onFechaChange,
        onHoraChange = vm::onHoraChange,
        onLugarChange = vm::onLugarChange,
        onNotasChange = vm::onNotasChange,
        onRecordatorioChange = vm::onRecordatorioChange,
        onGuardar = vm::onGuardar,
        modifier = modifier,
    )
}
