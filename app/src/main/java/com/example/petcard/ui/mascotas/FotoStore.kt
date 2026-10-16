package com.example.petcard.ui.mascotas

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Persistencia de la foto elegida en el formulario.
 *
 * El photo picker entrega permisos temporales: `takePersistableUriPermission`
 * no aplica y la `content://` dejaría de leerse después de reiniciar. Por eso
 * la imagen se copia al almacenamiento privado de la app (`filesDir/fotos`),
 * que sobrevive a muertes del proceso y a reinicios.
 */

private const val CARPETA_FOTOS = "fotos"

/** Devuelve la URI de la copia interna; si algo falla, la URI original. */
internal fun copiarFoto(contexto: Context, origen: Uri): String {
    val copia = runCatching { guardarCopia(contexto, origen) }.getOrNull()
    return copia?.let { Uri.fromFile(it).toString() } ?: origen.toString()
}

/** Borra la copia interna anterior (nunca toca `content://` ajenas a la app). */
internal fun borrarFotoInterna(contexto: Context, fotoUri: String?) {
    if (fotoUri == null || !fotoUri.startsWith("file:")) return
    val ruta = Uri.parse(fotoUri).path ?: return
    if (!ruta.startsWith(contexto.filesDir.path)) return
    runCatching { File(ruta).delete() }
}

private fun guardarCopia(contexto: Context, origen: Uri): File? {
    val dir = File(contexto.filesDir, CARPETA_FOTOS).apply { mkdirs() }
    val extension = contexto.contentResolver.getType(origen)
        ?.substringAfterLast('/', "")
        ?.takeIf { it.isNotBlank() } ?: "jpg"
    val archivo = File(dir, "mascota_${System.currentTimeMillis()}.$extension")
    val entrada = contexto.contentResolver.openInputStream(origen) ?: return null
    entrada.use { stream ->
        FileOutputStream(archivo).use { salida -> stream.copyTo(salida) }
    }
    if (archivo.length() == 0L) {
        archivo.delete()
        return null
    }
    return archivo
}
