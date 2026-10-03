package com.example.petcard

import android.app.Application
import com.example.petcard.notifications.Notificaciones
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de Hilt: todo el grafo de dependencias
 * (BD, DAOs, repositorio) vive en los módulos de `di/`.
 */
@HiltAndroidApp
class PetCardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificaciones.crearCanal(this)
    }
}
