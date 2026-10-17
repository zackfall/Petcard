package com.example.petcard.ui.mascotas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcard.data.local.MascotaEntity

@Composable
fun MascotasRoute(
    alAgregarMascota: () -> Unit,
    alEditar: (MascotaEntity) -> Unit,
    alNuevoEvento: (MascotaEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pide a Hilt el MascotasViewModel (lo crea con sus dependencias si no existe, o devuelve el que ya existe).
    val viewModel: MascotasViewModel = hiltViewModel()
    // Observa el estado del ViewModel (la lista de mascotas y si está cargando). Cada vez que cambia, la pantalla se redibuja.
    // Se detiene sola cuando la app está en segundo plano.
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    MascotasContent(
        estado = estado,
        onAgregarMascota = alAgregarMascota,
        onEditar = alEditar,
        onNuevoEvento = alNuevoEvento,
        onEliminar = viewModel::eliminar,
        modifier = modifier,
    )
}