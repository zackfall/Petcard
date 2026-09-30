package com.example.petcard

import android.app.Application
import com.example.petcard.di.AppContainer
import com.example.petcard.di.DefaultAppContainer
import com.example.petcard.notifications.Notificaciones

class PetCardApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        Notificaciones.crearCanal(this)
    }
}
