package com.example.petcard.ui.eventos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.ui.shared.EventoItem
import com.example.petcard.ui.shared.EventoRow
import com.example.petcard.ui.theme.PetcardTheme

/**
 * Vista SIN estado de Eventos pendientes (diseño screen-container-6).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 */
@Composable
fun EventosPendientesContent(
    estado: PendientesUiState,
    onHecho: (Long) -> Unit,
    onEliminar: (Long) -> Unit,
    onCrearEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearEvento) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo evento")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Eventos Pendientes",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            if (!estado.cargando && estado.eventos.isEmpty()) {
                item {
                    Text(
                        text = "Sin pendientes. ¡Buen trabajo!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(estado.eventos, key = { it.id }) { evento ->
                EventoRow(
                    titulo = evento.titulo,
                    subtitulo = evento.subtitulo,
                    detalle = evento.detalle,
                    accion = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { if (it) onHecho(evento.id) },
                            )
                            Text(
                                "Hecho (eliminar)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            IconButton(onClick = { onEliminar(evento.id) }) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "Eliminar evento",
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PendientesPreview() {
    PetcardTheme {
        EventosPendientesContent(
            estado = PendientesUiState(
                eventos = listOf(
                    EventoItem(1, "Vacuna de la Rabia", "Max • Vacuna 24 oct 2026", "Recordar llevar carnet físico."),
                    EventoItem(2, "Chequeo de Peso", "Luna • Control 28 oct 2026", "Llevar registro de alimentación."),
                ),
                cargando = false,
            ),
            onHecho = {},
            onEliminar = {},
            onCrearEvento = {},
        )
    }
}
