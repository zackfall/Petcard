package com.example.petcard.ui.config

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.petcard.notifications.Notificaciones

/**
 * Activa o desactiva los avisos de PetCard sobre el canal de recordatorios.
 *
 * La importancia del canal vive en el sistema, así que la preferencia
 * sobrevive a reinicios sin necesitar persistencia propia. La Route lee y
 * escribe aquí; el ViewModel nunca toca [Context] (regla de recordatorios).
 */
object NotificacionesPush {

    /** `true` = los recordatorios de salud se van a mostrar. */
    fun activas(contexto: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Sin canales: solo existe el bloqueo global de la app.
            return NotificationManagerCompat.from(contexto).areNotificationsEnabled()
        }
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return true
        val canal = manager.getNotificationChannel(Notificaciones.CANAL_ID) ?: return true
        return canal.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun aplicar(contexto: Context, activas: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (activas(contexto) == activas) return
        val manager = contexto.getSystemService(NotificationManager::class.java) ?: return
        // Se recrea el canal: es la única forma de subir o bajar su importancia.
        manager.deleteNotificationChannel(Notificaciones.CANAL_ID)
        manager.createNotificationChannel(
            NotificationChannel(
                Notificaciones.CANAL_ID,
                "Recordatorios de salud",
                if (activas) {
                    NotificationManager.IMPORTANCE_DEFAULT
                } else {
                    NotificationManager.IMPORTANCE_NONE
                },
            ),
        )
    }
}
