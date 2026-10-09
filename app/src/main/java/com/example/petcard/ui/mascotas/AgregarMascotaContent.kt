package com.example.petcard.ui.mascotas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcard.ui.theme.PetcardTheme

/**
 * Vista SIN estado del formulario Agregar Mascota (diseño screen-container-3).
 * Recibe estado inmutable + lambdas; jamás un ViewModel.
 */
@Composable
fun AgregarMascotaContent(
    estado: AgregarMascotaUiState,
    onNombreChange: (String) -> Unit,
    onEspecieChange: (String) -> Unit,
    onRazaChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onElegirFoto: () -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- Foto de la mascota ---
        FotoMascota(
            fotoUri = estado.fotoUri,
            modifier = Modifier
                .size(76.dp)
                .clickable(onClick = onElegirFoto),
            forma = RoundedCornerShape(14.dp),
            iconoPlaceholder = Icons.Outlined.Image,
            tamanoIcono = 38.dp,
        )
        Text(
            text = "FOTO DE LA MASCOTA",
            color = VerdePetCard,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
        )

        // --- Nombre ---
        CampoTexto(
            etiqueta = "Nombre",
            valor = estado.nombre,
            onValorChange = onNombreChange,
            placeholder = "Ej. Max, Luna, Bobby",
        )

        // --- Especie / Tipo ---
        CampoTexto(
            etiqueta = "Especie / Tipo",
            valor = estado.especie,
            onValorChange = onEspecieChange,
            placeholder = "Ej. Perro, Gato, Ave, etc.",
        )

        // --- Raza ---
        CampoTexto(
            etiqueta = "Raza",
            valor = estado.raza,
            onValorChange = onRazaChange,
            placeholder = "Ej. Golden Retriever, Criollo",
        )

        // --- Edad ---
        CampoTexto(
            etiqueta = "Edad (Años)",
            valor = estado.edadAnos,
            onValorChange = onEdadChange,
            placeholder = "Ej. 3",
            keyboardType = KeyboardType.Number,
        )

        Button(
            onClick = onGuardar,
            enabled = estado.puedeGuardar,
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeBoton,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(if (estado.guardando) "Guardando..." else "Listo ✓")
        }
    }
}

/** Campo del formulario: etiqueta arriba + recuadro con placeholder (diseño). */
@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AgregarMascotaPreview() {
    PetcardTheme {
        AgregarMascotaContent(
            estado = AgregarMascotaUiState(),
            onNombreChange = {},
            onEspecieChange = {},
            onRazaChange = {},
            onEdadChange = {},
            onElegirFoto = {},
            onGuardar = {},
        )
    }
}
