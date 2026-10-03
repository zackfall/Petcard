package com.example.petcard.ui.inicio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InicioRoute(
    alVerTodos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InicioViewModel = hiltViewModel()
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    InicioContent(estado = estado, onVerTodos = alVerTodos, modifier = modifier)
}