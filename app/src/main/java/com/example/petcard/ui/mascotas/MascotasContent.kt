package com.example.petcard.ui.mascotas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.ui.theme.PetcardTheme

@Composable
fun MascotasContent(
    estado: MascotasUiState,
    onAgregarMascota: () -> Unit,
    onEditar: (MascotaEntity) -> Unit,
    onNuevoEvento: (MascotaEntity) -> Unit,
    onEliminar: (MascotaEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Button(onClick = onAgregarMascota, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Agregar Mascota")
            }
        }
        item { Text("Tus Mascotas", style = MaterialTheme.typography.titleMedium) }

        if (!estado.cargando && estado.mascotas.isEmpty()) {
            item {
                Text(
                    "Aún no tienes mascotas registradas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(estado.mascotas, key = { it.id }) { mascota ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = mascota.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { onEditar(mascota) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onNuevoEvento(mascota) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar evento")
                    }
                    IconButton(onClick = { onEliminar(mascota) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MascotasPreview() {
    PetcardTheme {
        MascotasContent(
            estado = MascotasUiState(
                mascotas = listOf(
                    MascotaEntity(
                        id = 1,
                        nombre = "Max",
                        especie = "Perro",
                        raza = "Golden Retriever",
                        fechaNacimientoMillis = 0L,
                        pesoKg = 32.5,
                    ),
                    MascotaEntity(
                        id = 2,
                        nombre = "Luna",
                        especie = "Gato",
                        raza = "Siamés",
                        fechaNacimientoMillis = 0L,
                        pesoKg = 4.2,
                    ),
                ),
                cargando = false,
            ),
            onAgregarMascota = {},
            onEditar = {},
            onNuevoEvento = {},
            onEliminar = {},
        )
    }
}