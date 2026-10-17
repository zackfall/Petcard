package com.example.petcard.ui.config

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcard.notifications.Notificaciones

/**
 * Contenedor CON estado de Configuración: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()` y traduce el evento
 * puntual `NotificacionesPush` en activar/desactivar el canal del sistema
 * (pidiendo `POST_NOTIFICATIONS` antes si hace falta en API 33+).
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
    val oscuroSistema = isSystemInDarkTheme()

    fun refrescarAvisos() {
        vm.inicializar(
            notificacionesPush = Notificaciones.avisosActivos(contexto),
            modoOscuroSistema = oscuroSistema,
        )
    }

    val pedirAvisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concedido ->
        if (concedido) Notificaciones.aplicarAvisos(contexto, true)
        refrescarAvisos()
    }

    LaunchedEffect(vm) {
        refrescarAvisos()
        vm.eventos.collect { evento ->
            when (evento) {
                is ConfiguracionEvent.NotificacionesPush -> {
                    val faltaPermiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !NotificationManagerCompat.from(contexto).areNotificationsEnabled()
                    if (evento.activas && faltaPermiso) {
                        pedirAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        Notificaciones.aplicarAvisos(contexto, evento.activas)
                        refrescarAvisos()
                    }
                }
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
