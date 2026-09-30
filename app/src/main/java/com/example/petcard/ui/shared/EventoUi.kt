package com.example.petcard.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.data.local.EventoEntity
import com.example.petcard.data.local.TipoEvento
import com.example.petcard.ui.theme.PetcardTheme
import com.example.petcard.util.Dates

/** Etiqueta visible para cada [TipoEvento]. Uso único en toda la app. */
fun TipoEvento.etiqueta(): String = when (this) {
    TipoEvento.VACUNA -> "Vacuna"
    TipoEvento.DESPARASITACION -> "Desparasitación"
    TipoEvento.CONTROL_VETERINARIO -> "Control veterinario"
    TipoEvento.MEDICACION -> "Medicación"
    TipoEvento.BANO -> "Baño"
    TipoEvento.OTRO -> "Otro"
}

/**
 * Fila sin estado para mostrar un evento (la usan Calendario y Pendientes).
 * Solo recibe datos inmutables y lambdas: jamás un ViewModel.
 */
@Composable
fun EventoRow(
    titulo: String,
    subtitulo: String,
    detalle: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium)
                Text(
                    detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            accion?.invoke()
        }
    }
}

/** "Max • Vacuna Oct 24, 2026" + "10:00 • Clínica..." para reutilizar formato. */
fun lineaEvento(
    nombreMascota: String,
    tipo: TipoEvento,
    fechaMillis: Long,
    horaMinutos: Int?,
    lugar: String?,
): Pair<String, String> {
    val subtitulo = "$nombreMascota • ${tipo.etiqueta()} ${Dates.formatearFecha(fechaMillis)}"
    val detalle = listOfNotNull(
        horaMinutos?.let { Dates.formatearHora(it) },
        lugar?.takeIf { it.isNotBlank() },
    ).joinToString(" • ")
    return subtitulo to detalle
}

/** Item plano listo para pintar (lo arman los ViewModels, no la UI). */
data class EventoItem(
    val id: Long,
    val titulo: String,
    val subtitulo: String,
    val detalle: String,
    val hecho: Boolean = false,
    val tieneRecordatorio: Boolean = false,
)

/** Convierte una entidad en [EventoItem] resolviendo el nombre de la mascota. */
fun EventoEntity.aItem(nombreMascota: String): EventoItem {
    val (subtitulo, detalle) = lineaEvento(nombreMascota, tipo, fechaMillis, horaMinutos, lugar)
    return EventoItem(id, titulo, subtitulo, detalle, hecho, proximaMillis != null)
}

@Preview(showBackground = true)
@Composable
private fun EventoRowPreview() {
    PetcardTheme {
        EventoRow(
            titulo = "Vacuna de la Rabia",
            subtitulo = "Max • Vacuna 24 oct 2026",
            detalle = "10:00 • Clínica Veterinaria Principal",
        )
    }
}
