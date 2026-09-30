package com.example.petcard.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.petcard.ui.theme.PetcardTheme

/**
 * Marcador temporal para las rutas de otros integrantes.
 * Cada dueño reemplaza su `PlaceholderScreen` por su `*Route` real
 * en [PetCardScaffold], sin tocar el resto del shell.
 */
@Composable
fun PlaceholderScreen(nombre: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "$nombre\n(Pendiente: la implementa su dueño)",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderPreview() {
    PetcardTheme {
        PlaceholderScreen(nombre = "Inicio")
    }
}
