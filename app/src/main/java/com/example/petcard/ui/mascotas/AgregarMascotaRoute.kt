package com.example.petcard.ui.mascotas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Contenedor CON estado de Agregar Mascota: crea su ViewModel con Hilt,
 * consume el estado con `collectAsStateWithLifecycle()`, abre el selector de
 * fotos del sistema y traduce el evento puntual `Guardado` en navegar atrás.
 * Pasa solo estado + lambdas al Content.
 */
@Composable
fun AgregarMascotaRoute(
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: AgregarMascotaViewModel = hiltViewModel()
    val estado by vm.uiState.collectAsStateWithLifecycle()

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) vm.onFotoChange(uri.toString())
    }

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
        onElegirFoto = {
            selectorFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onGuardar = vm::onGuardar,
        modifier = modifier,
    )
}
