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
    //alGuardar: () -> Unit= Se ejecuta cuando la mascota se guardó. Normalmente es “navegar hacia atrás” o “ir a la lista”.
    // Así esta pantalla no necesita conocer la navegación
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    //hilt se encarga de buscar u obtener AgregarMascotaViewModel
    val vm: AgregarMascotaViewModel = hiltViewModel()
    // Convierte el StateFlow en un estado de Compose. Es mejor que collectAsState() porque deja de escuchar cuando la app está en segundo plano,
    // ahorrando recursos. Cada vez que el estado cambia, la pantalla se redibuja.
    val estado by vm.uiState.collectAsStateWithLifecycle()

    //Esto abre el selector de fotos del sistema y recibe el resultado.
    val selectorFoto = rememberLauncherForActivityResult(
        //PickVisualMedia= es el selector moderno de Android. No necesita permisos de almacenamiento.
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        // { uri -> ... }= se ejecuta cuando el usuario elige algo. Si eligió una foto (uri != null), se la pasa al ViewModel con onFotoChange, convertida a String.
        // Si canceló, uri es null y no pasa nada.
        if (uri != null) vm.onFotoChange(uri.toString())
    }

    //LaunchedEffect(vm): lanza una corrutina cuando la pantalla aparece, y la cancela cuando desaparece. La clave vm hace que solo se reinicie si cambia el ViewModel.
    LaunchedEffect(vm) {
        //collect { ... }: se queda esperando eventos.
        vm.eventos.collect { evento ->
            //cuando llega el evento de Guardar, se llama a alGuardar(), que navega a otra pantalla. Esto cierra el ciclo con el _eventos.send(...) de onGuardar().
            if (evento is AgregarMascotaEvent.Guardado) alGuardar()
        }
    }

    // Pasa todo a la parte visual
    AgregarMascotaContent(
        //estado = estado: le da los datos actuales del formulario.
        estado = estado,
        // vm::onNombreChange: es una referencia a función. Es equivalente a { valor -> vm.onNombreChange(valor) }, pero más corto
        onNombreChange = vm::onNombreChange,
        onEspecieChange = vm::onEspecieChange,
        onRazaChange = vm::onRazaChange,
        onEdadChange = vm::onEdadChange,
        // aquí sí se escribe una lambda, porque en lugar de llamar al ViewModel, abre el selector de fotos con selectorFoto.launch(...), limitado a solo imágenes.
        onElegirFoto = {
            selectorFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onGuardar = vm::onGuardar,
        modifier = modifier,
    )
}
