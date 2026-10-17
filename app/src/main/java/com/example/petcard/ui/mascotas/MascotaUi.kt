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
    // LocalContext.current: obtiene el Context de Android, necesario para leer archivos
    val contexto = LocalContext.current
    // produceState: ejecuta una corrutina y convierte su resultado en un estado de Compose. Empieza con null (initialValue) y cuando la foto termina de cargar,
    // asigna value = ... y la pantalla se redibuja sola.
    // key1 = fotoUri: si fotoUri cambia (el usuario elige otra foto), la corrutina se reinicia y carga la nueva.

    val bitmap = produceState<Bitmap?>(initialValue = null, key1 = fotoUri) {
        // fotoUri ?: return@produceState: si es null, no hay nada que cargar y se sale.
        // return@produceState es la forma de hacer return dentro de una lambda.

        val uri = fotoUri ?: return@produceState
        // withContext(Dispatchers.IO): cambia a un hilo de segundo plano. Leer un archivo es lento y no debe hacerse en el hilo principal, o la app se congela.
        // El comentario del código lo dice: “la lectura del content:// se hace en IO”.

        value = withContext(Dispatchers.IO) {

            // runCatching { ... }.getOrNull(): si algo falla (archivo borrado, sin permiso), no se cae la app: devuelve null y se muestra el icono.
            runCatching {

                // openInputStream(Uri.parse(uri)): abre el archivo que apunta ese URI (el que vino del selector de fotos).
                // .use { ... }: cierra el stream automáticamente al terminar, aunque haya error.
                contexto.contentResolver.openInputStream(Uri.parse(uri))?.use {

                    // BitmapFactory.decodeStream(it): convierte los bytes en un Bitmap (una imagen).
                    BitmapFactory.decodeStream(it)
                }
            }.getOrNull()
        }
    }

    // Box: apila elementos uno sobre otro; aquí sirve de marco.
    //.clip(forma): recorta el contenido con la forma (círculo o esquinas redondeadas).
    //.background(...): color de fondo gris suave del tema, visible detrás del icono.
    //if (foto != null): si ya cargó la foto, se dibuja con Image. Si no, se muestra el icono en verde.
    //ContentScale.Crop: la imagen llena todo el recuadro y recorta lo que sobre, sin deformarse.
    //asImageBitmap(): convierte el Bitmap de Android al tipo que usa Compose.

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

// Es el camino inverso de nacimientoPorEdad: de la fecha de nacimiento a la edad.
//
//Dates.desglosar separa los milisegundos en año, mes y día.
//Se calcula año actual - año de nacimiento.
//Pero si aún no llegó el cumpleaños este año, la mascota todavía no cumplió ese año, así que se resta 1. Eso comprueba el if: el mes de hoy es menor,
// o es el mismo mes pero el día es menor.
//coerceAtLeast(0) asegura que nunca devuelva negativo (por ejemplo, si la fecha estuviera en el futuro).
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

// Es una función de extensión: MascotaEntity.lineaPerfil() agrega una función nueva a MascotaEntity sin modificar su clase. Se usa como mascota.lineaPerfil().
//
//Calcula la edad y arma el texto, con singular o plural (“1 año” / “3 años”).
//Crea una lista con la raza y la edad, descarta los textos vacíos (isNotBlank) y los une con •.
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
