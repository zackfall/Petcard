package com.example.petcard.ui.mascotas

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.data.local.TipoEvento
import com.example.petcard.ui.shared.etiqueta
import com.example.petcard.ui.theme.PetcardTheme
import com.example.petcard.util.Dates

/**
 * Vista SIN estado del perfil de mascota (diseño screen-container-2).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 */
@Composable
fun PerfilMascotaContent(
    estado: PerfilMascotaUiState,
    onNuevoEvento: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            estado.cargando -> {
                Spacer(Modifier.height(48.dp))
                CircularProgressIndicator()
            }

            !estado.encontrada -> {
                Spacer(Modifier.height(48.dp))
                Text(
                    text = "No se encontró la mascota.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> {
                val mascota = estado.mascota!!

                FotoMascota(
                    fotoUri = mascota.fotoUri,
                    modifier = Modifier
                        .size(96.dp)
                        .border(1.dp, VerdePetCard.copy(alpha = 0.35f), CircleShape),
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = mascota.nombre,
                    color = VerdePetCard,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = estado.subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                TextButton(onClick = onNuevoEvento) {
                    Text(
                        text = "+ Nuevo Evento",
                        color = VerdePetCard,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    )
                }

                Text(
                    text = "Historial de Eventos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )

                if (estado.eventos.isEmpty()) {
                    Text(
                        text = "Aún no hay eventos registrados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                }

                estado.eventos.forEach { evento ->
                    ItemHistorial(evento = evento)
                }
            }
        }
    }
}

/** Fila del historial: círculo + título, fecha en verde y detalle (diseño). */
@Composable
private fun ItemHistorial(
    evento: EventoEntity,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(14.dp)
                .border(2.dp, VerdePetCard, CircleShape),
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(evento.titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                text = Dates.formatearFecha(evento.fechaMillis),
                color = VerdePetCard,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = detalleDe(evento),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/** Notas → lugar → etiqueta del tipo (reutiliza `ui/shared/EventoUi.kt`). */
private fun detalleDe(evento: EventoEntity): String =
    evento.notas?.takeIf { it.isNotBlank() }
        ?: evento.lugar?.takeIf { it.isNotBlank() }
        ?: evento.tipo.etiqueta()

@Preview(showBackground = true)
@Composable
private fun PerfilMascotaPreview() {
    PetcardTheme {
        PerfilMascotaContent(
            estado = PerfilMascotaUiState(
                cargando = false,
                mascota = MascotaEntity(
                    id = 1,
                    nombre = "Max",
                    especie = "Perro",
                    raza = "Golden Retriever",
                    fechaNacimientoMillis = Dates.hoyInicioMillis(),
                ),
                eventos = listOf(
                    EventoEntity(
                        id = 1,
                        mascotaId = 1,
                        tipo = TipoEvento.DESPARASITACION,
                        titulo = "Desparasitación Interna",
                        fechaMillis = Dates.hoyInicioMillis(),
                        notas = "Marca: NexGard Spectra",
                    ),
                    EventoEntity(
                        id = 2,
                        mascotaId = 1,
                        tipo = TipoEvento.BANO,
                        titulo = "Baño Médico",
                        fechaMillis = Dates.hoyInicioMillis() - 30 * Dates.MILLIS_POR_DIA,
                        notas = "Champú antialérgico",
                    ),
                ),
            ),
            onNuevoEvento = {},
        )
    }
}
