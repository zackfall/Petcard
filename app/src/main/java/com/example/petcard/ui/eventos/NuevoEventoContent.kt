package com.example.petcard.ui.eventos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.data.local.TipoEvento
import com.example.petcard.ui.shared.etiqueta
import com.example.petcard.ui.theme.PetcardTheme
import com.example.petcard.util.Dates
import java.util.Calendar

/**
 * Vista SIN estado del formulario Nuevo Evento (diseño screen-container-4).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEventoContent(
    estado: NuevoEventoUiState,
    onMascotaChange: (Long?) -> Unit,
    onTituloChange: (String) -> Unit,
    onTipoChange: (TipoEvento) -> Unit,
    onFechaChange: (Long) -> Unit,
    onHoraChange: (Int?) -> Unit,
    onLugarChange: (String) -> Unit,
    onNotasChange: (String) -> Unit,
    onRecordatorioChange: (Long?) -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandirMascotas by remember { mutableStateOf(false) }
    var expandirTipos by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }
    var mostrarRecordatorio by remember { mutableStateOf(false) }

    val mascotaElegida = estado.mascotas.firstOrNull { it.id == estado.mascotaId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // --- Mascota ---
        ExposedDropdownMenuBox(
            expanded = expandirMascotas,
            onExpandedChange = { expandirMascotas = it },
        ) {
            OutlinedTextField(
                value = mascotaElegida?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Mascota") },
                placeholder = { Text("Seleccionar mascota...") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandirMascotas) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = expandirMascotas,
                onDismissRequest = { expandirMascotas = false },
            ) {
                estado.mascotas.forEach { mascota ->
                    DropdownMenuItem(
                        text = { Text("${mascota.nombre} (${mascota.especie})") },
                        onClick = {
                            onMascotaChange(mascota.id)
                            expandirMascotas = false
                        },
                    )
                }
            }
        }

        // --- Título ---
        OutlinedTextField(
            value = estado.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título") },
            placeholder = { Text("Ej. Vacuna de la Rabia") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // --- Tipo de evento ---
        ExposedDropdownMenuBox(
            expanded = expandirTipos,
            onExpandedChange = { expandirTipos = it },
        ) {
            OutlinedTextField(
                value = estado.tipo?.etiqueta() ?: "Ninguno",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Evento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandirTipos) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = expandirTipos,
                onDismissRequest = { expandirTipos = false },
            ) {
                TipoEvento.entries.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.etiqueta()) },
                        onClick = {
                            onTipoChange(tipo)
                            expandirTipos = false
                        },
                    )
                }
            }
        }

        // --- Fecha y hora ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = { mostrarFecha = true },
                modifier = Modifier.weight(1f),
            ) {
                Text("Fecha\n${Dates.formatearFechaCorta(estado.fechaMillis)}")
            }
            OutlinedButton(
                onClick = { mostrarHora = true },
                modifier = Modifier.weight(1f),
            ) {
                Text("Hora\n${estado.horaMinutos?.let { Dates.formatearHora(it) } ?: "--:--"}")
            }
        }

        // --- Lugar (opcional) ---
        OutlinedTextField(
            value = estado.lugar,
            onValueChange = onLugarChange,
            label = { Text("Lugar / Clínica (opcional)") },
            placeholder = { Text("Ej. Clínica Veterinaria Principal") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // --- Notas ---
        OutlinedTextField(
            value = estado.notas,
            onValueChange = onNotasChange,
            label = { Text("Notas / Recordatorio") },
            placeholder = { Text("Añadir notas del evento...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        // --- Próxima fecha (aviso opcional) ---
        OutlinedButton(
            onClick = { mostrarRecordatorio = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Próximo recordatorio: " +
                    (estado.proximaMillis?.let { Dates.formatearFechaCorta(it) } ?: "sin aviso"),
            )
        }

        Button(
            onClick = onGuardar,
            enabled = estado.puedeGuardar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (estado.guardando) "Guardando..." else "Listo ✓")
        }
    }

    if (mostrarFecha) {
        val picker = rememberDatePickerState(
            initialSelectedDateMillis = Dates.paraDatePicker(estado.fechaMillis),
        )
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    picker.selectedDateMillis?.let { utc ->
                        val cal = Calendar.getInstance().apply { timeInMillis = utc }
                        onFechaChange(
                            Dates.inicioDiaMillis(
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH),
                            ),
                        )
                    }
                    mostrarFecha = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = picker)
        }
    }

    if (mostrarHora) {
        val (h0, m0) = (estado.horaMinutos ?: 600).let { it / 60 to it % 60 }
        val picker = rememberTimePickerState(initialHour = h0, initialMinute = m0)
        DatePickerDialog(
            onDismissRequest = { mostrarHora = false },
            confirmButton = {
                TextButton(onClick = {
                    onHoraChange(picker.hour * 60 + picker.minute)
                    mostrarHora = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onHoraChange(null)
                    mostrarHora = false
                }) { Text("Sin hora") }
            },
        ) {
            TimePicker(state = picker)
        }
    }

    if (mostrarRecordatorio) {
        val picker = rememberDatePickerState(
            initialSelectedDateMillis = Dates.paraDatePicker(
                estado.proximaMillis ?: estado.fechaMillis,
            ),
        )
        DatePickerDialog(
            onDismissRequest = { mostrarRecordatorio = false },
            confirmButton = {
                TextButton(onClick = {
                    picker.selectedDateMillis?.let { utc ->
                        val cal = Calendar.getInstance().apply { timeInMillis = utc }
                        onRecordatorioChange(
                            Dates.inicioDiaMillis(
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH),
                            ),
                        )
                    }
                    mostrarRecordatorio = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onRecordatorioChange(null)
                    mostrarRecordatorio = false
                }) { Text("Sin aviso") }
            },
        ) {
            DatePicker(state = picker)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NuevoEventoPreview() {
    PetcardTheme {
        NuevoEventoContent(
            estado = NuevoEventoUiState(
                mascotas = listOf(
                    MascotaEntity(
                        id = 1, nombre = "Max", especie = "Perro",
                        raza = "Golden Retriever", fechaNacimientoMillis = 0L,
                    ),
                ),
                mascotaId = 1,
                titulo = "Vacuna de la Rabia",
            ),
            onMascotaChange = {}, onTituloChange = {}, onTipoChange = {},
            onFechaChange = {}, onHoraChange = {}, onLugarChange = {},
            onNotasChange = {}, onRecordatorioChange = {}, onGuardar = {},
        )
    }
}
