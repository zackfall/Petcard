package com.example.petcard.ui.shell

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Un destino del BottomBar. El shell es dueño de esta lista; si otro
 * integrante necesita un destino nuevo, lo propone al dueño del shell.
 */
data class DestinoBottomBar(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector,
)

@Composable
fun PetCardBottomBar(
    destinos: List<DestinoBottomBar>,
    rutaActual: String?,
    alNavegar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        destinos.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = { alNavegar(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                label = { Text(destino.etiqueta) },
            )
        }
    }
}
