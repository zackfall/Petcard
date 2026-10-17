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

//Intenta guardar la copia dentro de runCatching, así que si hay un error no se cierra la app, solo devuelve null.
//Si la copia salió bien, devuelve su dirección como texto (file:///...).
//Si algo falló, devuelve la uri original como plan B. Es mejor tener una foto temporal que ninguna.
internal fun copiarFoto(contexto: Context, origen: Uri): String {
    val copia = runCatching { guardarCopia(contexto, origen) }.getOrNull()
    return copia?.let { Uri.fromFile(it).toString() } ?: origen.toString()
}

/** Borra la copia interna anterior (nunca toca `content://` ajenas a la app). */
//¿Es null o no empieza con file:? Entonces no es una copia nuestra (puede ser una content:// de la galería) → no hace nada.
//¿No se puede obtener la ruta? → no hace nada.
//¿La ruta no está dentro de filesDir? → no hace nada, porque no es un archivo de la app.
internal fun borrarFotoInterna(contexto: Context, fotoUri: String?) {
    if (fotoUri == null || !fotoUri.startsWith("file:")) return
    val ruta = Uri.parse(fotoUri).path ?: return
    if (!ruta.startsWith(contexto.filesDir.path)) return
    runCatching { File(ruta).delete() }
}

private fun guardarCopia(contexto: Context, origen: Uri): File? {
    //Crea la carpeta fotos dentro del almacenamiento privado si no existe (mkdirs()). CARPETA_FOTOS es una constante privada con el nombre "fotos".
    val dir = File(contexto.filesDir, CARPETA_FOTOS).apply { mkdirs() }

    //Le pregunta al sistema qué tipo de archivo es (por ejemplo image/png), se queda con lo que va después de la / (png) y lo usa de extensión.
    // Si no puede saberlo, usa "jpg" por defecto.
    val extension = contexto.contentResolver.getType(origen)
        ?.substringAfterLast('/', "")
        ?.takeIf { it.isNotBlank() } ?: "jpg"

    //Usa la hora actual en milisegundos, por ejemplo mascota_1729123456789.png. Así dos fotos nunca se pisan.
    val archivo = File(dir, "mascota_${System.currentTimeMillis()}.$extension")

    //Abre la foto original para leerla (openInputStream). Si no puede, devuelve null.
    //Abre el archivo nuevo para escribirlo (FileOutputStream).
    //copyTo pasa todos los datos de uno al otro.
    //use { } cierra automáticamente los flujos al terminar, incluso si hay un error. Esto evita fugas de recursos.
    val entrada = contexto.contentResolver.openInputStream(origen) ?: return null
    entrada.use { stream ->
        FileOutputStream(archivo).use { salida -> stream.copyTo(salida) }
    }
    // Si la copia pesa 0 bytes, algo salió mal: se borra el archivo defectuoso y se devuelve null (lo que activa el plan B de copiarFoto).
    if (archivo.length() == 0L) {
        archivo.delete()
        return null
    }
    return archivo
}
