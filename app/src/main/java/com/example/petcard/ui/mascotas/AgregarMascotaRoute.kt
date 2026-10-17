package com.example.petcard.ui.mascotas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Contenedor CON estado de Agregar Mascota: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()`, abre el selector de
 * fotos del sistema (y copia la elegida al almacenamiento interno, en IO),
 * y traduce el evento puntual `Guardado` en navegar atrás.
 * Pasa solo estado + lambdas al Content.
 */
@Composable
// alGuardar es lo que se ejecuta al terminar de guardar (normalmente, volver atrás) y modifier permite ajustar el diseño desde fuera.
fun AgregarMascotaRoute(
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Hilt te crea el ViewModel automáticamente. Ahí vive la información del formulario (nombre, especie, raza, etc.) y sobrevive aunque rotes la pantalla.
    val vm: AgregarMascotaViewModel = hiltViewModel()

    // Cada vez que cambia algo (por ejemplo, el usuario escribe un nombre), la pantalla se redibuja sola. El WithLifecycle hace que deje de escuchar cuando la app está
    // en segundo plano, para ahorrar recursos.
    val estado by vm.uiState.collectAsStateWithLifecycle()

    // El Context es el objeto de Android que da acceso al sistema: archivos, carpetas de la app, recursos. Para copiar y borrar archivos hace falta,
    // y por eso se obtiene aquí. LocalContext.current es la forma de pedirlo dentro de Compose.
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    // Esto abre la galería del sistema para que el usuario elija una foto. Cuando elige una, el bloque { uri -> ... } se ejecuta con la dirección (uri) de esa imagen.
    // Si no elige nada, uri es null y no pasa nada.
    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            val anterior = estado.fotoUri
            alcance.launch {
                // Con withContext(Dispatchers.IO) trabaja en un hilo de entrada/salida (para no congelar la pantalla): borra la foto vieja y copia la nueva al
                // almacenamiento interno de tu app.
                val destino = withContext(Dispatchers.IO) {
                    borrarFotoInterna(contexto, anterior)
                    copiarFoto(contexto, uri)
                }
                vm.onFotoChange(destino)
            }
        }
    }

    // Cuando el ViewModel termina de guardar la mascota, manda un evento de una sola vez (“Guardado”). Aquí lo escuchas y llamas a alGuardar(),
    // que normalmente navega hacia atrás. LaunchedEffect asegura que esto se haga una sola vez y no en cada redibujado.
    LaunchedEffect(vm) {
        vm.eventos.collect { evento ->
            if (evento is AgregarMascotaEvent.Guardado) alGuardar()
        }
    }

    AgregarMascotaContent(
        estado = estado,
        onNombreChange = vm::onNombreChange,
        onEspecieChange = vm::onEspecieChange,
        onRazaChange = vm::onRazaChange,
        onEdadChange = vm::onEdadChange,
        onPesoChange = vm::onPesoChange,
        onElegirFoto = {
            selectorFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onGuardar = vm::onGuardar,
        modifier = modifier,
    )
}
