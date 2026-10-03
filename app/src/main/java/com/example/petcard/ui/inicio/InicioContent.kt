package com.example.petcard.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import com.example.petcard.ui.theme.PetcardTheme

@Composable
fun InicioContent(
    estado: InicioUiState,
    onVerTodos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("Próximos Eventos de Salud", style = MaterialTheme.typography.titleMedium) }

        if (!estado.cargando && estado.proximos.isEmpty()) {
            item {
                Text(
                    "No hay eventos próximos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(estado.proximos, key = { "p${it.id}" }) { evento ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(evento.titulo, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${evento.mascota} • ${evento.fecha}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Historial Reciente", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onVerTodos) { Text("Ver todos") }
            }
        }

        if (!estado.cargando && estado.historial.isEmpty()) {
            item {
                Text(
                    "Aún no hay historial.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(estado.historial, key = { "h${it.id}" }) { evento ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(evento.titulo, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        evento.mascota,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(evento.fecha, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InicioPreview() {
    PetcardTheme {
        InicioContent(
            estado = InicioUiState(
                proximos = listOf(
                    EventoInicio(1, "Vacuna de la Rabia", "Max", "24 oct 2026"),
                    EventoInicio(2, "Chequeo de Peso", "Luna", "28 oct 2026"),
                ),
                historial = listOf(
                    EventoInicio(3, "Visita al Veterinario - Control", "Max", "15 oct 2026"),
                    EventoInicio(4, "Desparasitación interna", "Luna", "1 oct 2026"),
                ),
                cargando = false,
            ),
            onVerTodos = {},
        )
    }
}