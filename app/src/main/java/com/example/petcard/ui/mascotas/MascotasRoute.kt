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
    val viewModel: MascotasViewModel = hiltViewModel()
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