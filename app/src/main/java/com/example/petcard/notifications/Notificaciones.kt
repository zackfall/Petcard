package com.example.petcard.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat

/**
 * Recordatorios locales (requisito 11): una alarma por evento con
 * `proximaMillis` dispara [RecordatorioReceiver], que muestra la notificación.
 * Todo funciona sin internet. No sobrevive a reinicio (mejora futura).
 *
 * También es el dueño único del canal: [avisosActivos] y [aplicarAvisos]
 * son los que usa la pantalla de Configuración para el switch de avisos
 * (la Route los invoca; el ViewModel jamás toca `Context`).
 */
object Notificaciones {
    const val CANAL_ID = "recordatorios"
    private const val CANAL_NOMBRE = "Recordatorios de salud"
    private const val EXTRA_ID = "eventoId"
    private const val EXTRA_TITULO = "titulo"
    private const val EXTRA_DETALLE = "detalle"

    fun crearCanal(contexto: Context) {
        // Los canales no existen antes de API 26 (minSdk 24): sin este guard,
        // la app moriría al arrancar en Android 7.x.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CANAL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CANAL_ID,
                    CANAL_NOMBRE,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
    }

    /** `true` = los avisos de salud se van a mostrar (canal activo y no bloqueados). */
    fun avisosActivos(contexto: Context): Boolean {
        if (!NotificationManagerCompat.from(contexto).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return true
        val canal = manager.getNotificationChannel(CANAL_ID) ?: return true
        return canal.importance != NotificationManager.IMPORTANCE_NONE
    }

    /**
     * Activa o desactiva el canal de recordatorios. Es la única forma de
     * subir o bajar su importancia: se recrea el canal (persiste en el
     * sistema, así que la preferencia sobrevive a reinicios).
     * En API < 26 no hay canales: el bloqueo es global y vive en Ajustes.
     */
    fun aplicarAvisos(contexto: Context, activos: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return
        val canalActivo = (manager.getNotificationChannel(CANAL_ID)?.importance
            ?: NotificationManager.IMPORTANCE_DEFAULT) != NotificationManager.IMPORTANCE_NONE
        if (canalActivo == activos) return
        manager.deleteNotificationChannel(CANAL_ID)
        manager.createNotificationChannel(
            NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                if (activos) {
                    NotificationManager.IMPORTANCE_DEFAULT
                } else {
                    NotificationManager.IMPORTANCE_NONE
                },
            ),
        )
    }

    /** Programa el aviso. `requestCode = eventoId` para poder cancelarlo. */
    fun programar(
        contexto: Context,
        eventoId: Long,
        titulo: String,
        detalle: String,
        triggerMillis: Long,
    ) {
        val pi = pendingIntent(contexto, eventoId, titulo, detalle)
        val alarmas = contexto.getSystemService(AlarmManager::class.java) ?: return
        alarmas.set(AlarmManager.RTC_WAKEUP, triggerMillis, pi)
    }

    fun cancelar(contexto: Context, eventoId: Long) {
        val pi = pendingIntent(contexto, eventoId, "", "")
        contexto.getSystemService(AlarmManager::class.java)?.cancel(pi)
    }

    private fun pendingIntent(
        contexto: Context,
        eventoId: Long,
        titulo: String,
        detalle: String,
    ): PendingIntent {
        val intent = Intent(contexto, RecordatorioReceiver::class.java).apply {
            putExtra(EXTRA_ID, eventoId)
            putExtra(EXTRA_TITULO, titulo)
            putExtra(EXTRA_DETALLE, detalle)
        }
        return PendingIntent.getBroadcast(
            contexto,
            eventoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    internal fun leerExtras(intent: Intent): Triple<Long, String, String> =
        Triple(
            intent.getLongExtra(EXTRA_ID, 0L),
            intent.getStringExtra(EXTRA_TITULO) ?: "PetCard",
            intent.getStringExtra(EXTRA_DETALLE) ?: "",
        )
}
