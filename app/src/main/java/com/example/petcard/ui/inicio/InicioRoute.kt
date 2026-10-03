package com.example.petcard.ui.inicio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petcard.data.PetRepository

@Composable
fun InicioRoute(
    repositorio: PetRepository,
    alVerTodos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InicioViewModel = viewModel(factory = InicioViewModel.factory(repositorio))
    val estado by viewModel.estado.collectAsState()
    InicioContent(estado = estado, onVerTodos = alVerTodos, modifier = modifier)
}