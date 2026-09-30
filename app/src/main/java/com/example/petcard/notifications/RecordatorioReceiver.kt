package com.example.petcard.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/** Recibe la alarma de [Notificaciones] y muestra el aviso al usuario. */
class RecordatorioReceiver : BroadcastReceiver() {
    override fun onReceive(contexto: Context, intent: Intent) {
        Notificaciones.crearCanal(contexto)
        val (eventoId, titulo, detalle) = Notificaciones.leerExtras(intent)
        val notificacion = NotificationCompat.Builder(contexto, Notificaciones.CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(detalle.ifBlank { "Tienes un evento de salud pendiente." })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = contexto.getSystemService(NotificationManager::class.java)
        manager?.notify(eventoId.toInt(), notificacion)
    }
}
