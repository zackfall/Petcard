package com.example.petcard.ui.mascotas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity

@Composable
fun MascotasRoute(
    repositorio: PetRepository,
    alAgregarMascota: () -> Unit,
    alEditar: (MascotaEntity) -> Unit,
    alNuevoEvento: (MascotaEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MascotasViewModel = viewModel(factory = MascotasViewModel.factory(repositorio))
    val estado by viewModel.estado.collectAsState()

    MascotasContent(
        estado = estado,
        onAgregarMascota = alAgregarMascota,
        onEditar = alEditar,
        onNuevoEvento = alNuevoEvento,
        onEliminar = viewModel::eliminar,
        modifier = modifier,
    )
}