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
    //estado: contiene la lista de mascotas (estado.mascotas) y si se está cargando (estado.cargando).
    estado: MascotasUiState,
    // Los on...: avisan cuando el usuario toca algo. Fíjate que onEditar, onNuevoEvento y onEliminar reciben la mascota (MascotaEntity),
    // para que quien los use sepa sobre cuál mascota se hizo la acción.
    onAgregarMascota: () -> Unit,
    onEditar: (MascotaEntity) -> Unit,
    onNuevoEvento: (MascotaEntity) -> Unit,
    onEliminar: (MascotaEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Es una columna perezosa (lazy): solo dibuja los elementos que se ven en pantalla. Si tuvieras 500 mascotas, no crea 500 tarjetas,
    // sino unas pocas y las va reutilizando al hacer scroll. Además ya trae scroll incorporado, por eso aquí no necesitas verticalScroll(rememberScrollState())
    // como en el formulario.
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Un botón de ancho completo con un icono “+” y texto. Al pulsarlo llama onAgregarMascota (normalmente navega a la pantalla del formulario que ya vimos).
        // El contentDescription = null es correcto aquí porque el texto ya describe la acción, y el icono es solo decorativo.
        item {
            Button(onClick = onAgregarMascota, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Agregar Mascota")
            }
        }
        item { Text("Tus Mascotas", style = MaterialTheme.typography.titleMedium) }

        //Solo aparece si ya terminó de cargar y no hay mascotas. La condición !estado.cargando evita que el mensaje parpadee un instante antes de que lleguen los datos
        // de la base de datos. El color onSurfaceVariant es un gris suave del tema.
        if (!estado.cargando && estado.mascotas.isEmpty()) {
            item {
                Text(
                    "Aún no tienes mascotas registradas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // key = { it.id }: le da a cada tarjeta una identidad única (el id de la mascota). Así, si se elimina o agrega una, Compose sabe exactamente cuál cambió y no
        // redibuja todas. También mantiene bien el scroll y las animaciones.
        items(estado.mascotas, key = { it.id }) { mascota ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Text(mascota.nombre, modifier = Modifier.weight(1f)): el nombre ocupa todo el espacio sobrante, empujando los tres iconos hacia la derecha.
                    Text(
                        text = mascota.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    // Los tres IconButton: editar (lápiz), agregar evento (+) y eliminar (papelera). Cada uno llama su función pasando esa mascota: onEditar(mascota).
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