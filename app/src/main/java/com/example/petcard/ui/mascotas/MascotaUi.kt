package com.example.petcard.ui.mascotas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.ui.theme.PetcardTheme
import com.example.petcard.util.Dates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/** Verde de acento del diseño (screen-container-2 / screen-container-3). */
internal val VerdePetCard = Color(0xFF17A085)

/** Verde del botón principal "Listo ✓". */
internal val VerdeBoton = Color(0xFF5BC98C)

/**
 * Avatar de la mascota: pinta la foto elegida en el formulario o, si aún no
 * hay foto, el icono del diseño. La lectura del `content://` se hace en IO.
 */
@Composable
internal fun FotoMascota(
    fotoUri: String?,
    modifier: Modifier = Modifier,
    forma: Shape = CircleShape,
    iconoPlaceholder: ImageVector = Icons.Outlined.Pets,
    tamanoIcono: Dp = 48.dp,
) {
    val contexto = LocalContext.current
    val bitmap = produceState<Bitmap?>(initialValue = null, key1 = fotoUri) {
        val uri = fotoUri ?: return@produceState
        value = withContext(Dispatchers.IO) {
            runCatching {
                contexto.contentResolver.openInputStream(Uri.parse(uri))?.use {
                    BitmapFactory.decodeStream(it)
                }
            }.getOrNull()
        }
    }

    Box(
        modifier = modifier
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        val foto = bitmap.value
        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = iconoPlaceholder,
                contentDescription = null,
                modifier = Modifier.size(tamanoIcono),
                tint = VerdePetCard,
            )
        }
    }
}

/** Edad completa en años al día de hoy (sin `java.time`: minSdk 24). */
internal fun edadEnAnos(nacimientoMillis: Long): Int {
    val hoy = Calendar.getInstance()
    val (anio, mes, dia) = Dates.desglosar(nacimientoMillis)
    var edad = hoy.get(Calendar.YEAR) - anio
    val mesHoy = hoy.get(Calendar.MONTH) + 1
    if (mesHoy < mes || (mesHoy == mes && hoy.get(Calendar.DAY_OF_MONTH) < dia)) {
        edad--
    }
    return edad.coerceAtLeast(0)
}

/** Subtítulo del perfil: "Golden Retriever • 3 años" (diseño). */
internal fun MascotaEntity.lineaPerfil(): String {
    val edad = edadEnAnos(fechaNacimientoMillis)
    val textoEdad = if (edad == 1) "1 año" else "$edad años"
    return listOf(raza.trim(), textoEdad).filter { it.isNotBlank() }.joinToString(" • ")
}

@Preview(showBackground = true)
@Composable
private fun FotoMascotaPreview() {
    PetcardTheme {
        FotoMascota(fotoUri = null, modifier = Modifier.size(96.dp))
    }
}
