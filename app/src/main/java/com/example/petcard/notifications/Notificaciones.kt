package com.example.petcard.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Recordatorios locales (requisito 11): una alarma por evento con
 * `proximaMillis` dispara [RecordatorioReceiver], que muestra la notificación.
 * Todo funciona sin internet. No sobrevive a reinicio (mejora futura).
 */
object Notificaciones {
    const val CANAL_ID = "recordatorios"
    private const val EXTRA_ID = "eventoId"
    private const val EXTRA_TITULO = "titulo"
    private const val EXTRA_DETALLE = "detalle"

    fun crearCanal(contexto: Context) {
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CANAL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CANAL_ID,
                    "Recordatorios de salud",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
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
