package com.example.petcard.ui.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcard.ui.theme.PetcardTheme

/**
 * Vista SIN estado de Configuración (diseño screen-container-7).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 */
@Composable
fun ConfiguracionContent(
    estado: ConfiguracionUiState,
    onModoOscuroChange: (Boolean) -> Unit,
    onNotificacionesPushChange: (Boolean) -> Unit,
    onPrivacidad: () -> Unit,
    onAcerca: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item { TituloSeccion("Aspecto de la app") }

        item {
            FilaAjuste(
                icono = Icons.Filled.WbSunny,
                titulo = "Modo Claro / Oscuro",
            ) {
                // El tema aún no se cambia: el switch solo se muestra en pantalla.
                Switch(
                    checked = estado.modoOscuro,
                    onCheckedChange = onModoOscuroChange,
                )
            }
        }

        item { TituloSeccion("Ajustes generales") }

        item {
            FilaAjuste(
                icono = Icons.Filled.Notifications,
                titulo = "Notificaciones Push",
            ) {
                Switch(
                    checked = estado.notificacionesPush,
                    onCheckedChange = onNotificacionesPushChange,
                )
            }
        }

        item {
            FilaAjuste(
                icono = Icons.Filled.PrivacyTip,
                titulo = "Privacidad de datos",
                onClick = onPrivacidad,
            ) {
                EtiquetaAcceso("Gestionar")
            }
        }

        item {
            FilaAjuste(
                icono = Icons.Filled.Info,
                titulo = "Acerca de PetCard",
                onClick = onAcerca,
            ) {
                EtiquetaAcceso("Ver más")
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun EtiquetaAcceso(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FilaAjuste(
    icono: ImageVector,
    titulo: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfiguracionPreview() {
    PetcardTheme {
        ConfiguracionContent(
            estado = ConfiguracionUiState(
                modoOscuro = false,
                notificacionesPush = true,
            ),
            onModoOscuroChange = {},
            onNotificacionesPushChange = {},
            onPrivacidad = {},
            onAcerca = {},
            onCerrarSesion = {},
        )
    }
}
