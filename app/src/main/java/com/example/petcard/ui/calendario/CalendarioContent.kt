package com.example.petcard.ui.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.ui.shared.EventoItem
import com.example.petcard.ui.shared.EventoRow
import com.example.petcard.ui.theme.PetcardTheme
import com.example.petcard.util.Dates

private val DIAS_SEMANA = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sá")

/**
 * Vista SIN estado del Calendario (diseño screen-container-5).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 *
 * Nota: la grilla usa Column+Row (no LazyVerticalGrid) porque una grilla
 * perezosa anidada dentro del LazyColumn se mide con altura infinita y crashea.
 */
@Composable
fun CalendarioContent(
    estado: CalendarioUiState,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaElegido: (Long) -> Unit,
    onAlternarRecordatorio: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = onMesAnterior) {
                    Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = "${Dates.nombreMes(estado.anio, estado.mes)} ${estado.anio}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = onMesSiguiente) {
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                DIAS_SEMANA.forEach { dia ->
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                estado.celdas.chunked(7).forEach { semana ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        semana.forEach { celda ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.weight(1f),
                            ) {
                                if (celda.numero != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    celda.seleccionado -> MaterialTheme.colorScheme.primaryContainer
                                                    celda.esHoy -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> MaterialTheme.colorScheme.surface
                                                },
                                            )
                                            .clickable(enabled = celda.millis != null) {
                                                celda.millis?.let(onDiaElegido)
                                            }
                                            .padding(2.dp),
                                    ) {
                                        Text(
                                            text = celda.numero.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (celda.tieneEventos) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.surface
                                                    },
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                        // Completa la última semana si el mes no termina en sábado.
                        repeat(7 - semana.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            val (anio, mes, dia) = Dates.desglosar(estado.diaSeleccionado)
            Text(
                text = "Eventos para hoy ($dia de ${Dates.nombreMes(anio, mes)})",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        if (estado.eventos.isEmpty()) {
            item {
                Text(
                    text = "Sin eventos este día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(estado.eventos, key = { it.id }) { evento ->
                EventoRow(
                    titulo = evento.titulo,
                    subtitulo = evento.subtitulo,
                    detalle = evento.detalle,
                    accion = {
                        IconButton(onClick = { onAlternarRecordatorio(evento.id) }) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Alternar recordatorio",
                                tint = if (evento.tieneRecordatorio) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarioPreview() {
    PetcardTheme {
        CalendarioContent(
            estado = CalendarioUiState(
                anio = 2026,
                mes = 10,
                celdas = (1..31).map {
                    CeldaDia(it, 0L, tieneEventos = it == 24, esHoy = it == 24, seleccionado = it == 24)
                },
                eventos = listOf(
                    EventoItem(
                        1, "Vacuna de Rabia (Max)", "Max • Vacuna 24 oct 2026",
                        "10:00 • Clínica Veterinaria Principal", tieneRecordatorio = true,
                    ),
                ),
                cargando = false,
            ),
            onMesAnterior = {}, onMesSiguiente = {}, onDiaElegido = {},
            onAlternarRecordatorio = {},
        )
    }
}
