package com.example.petcard.di

import android.content.Context
import com.example.petcard.data.local.EventoDao
import com.example.petcard.data.local.MascotaDao
import com.example.petcard.data.local.PetCardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Grafo de la base de datos Room.
 *
 * REGLA DE EQUIPO: ningún ViewModel instancia la BD ni el repositorio.
 * Los ViewModels reciben `PetRepository` por constructor vía `@HiltViewModel`
 * + `@Inject` y se crean con `hiltViewModel()`.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun proporcionarBaseDatos(@ApplicationContext contexto: Context): PetCardDatabase =
        PetCardDatabase.obtener(contexto)

    @Provides
    fun proporcionarMascotaDao(baseDatos: PetCardDatabase): MascotaDao =
        baseDatos.mascotaDao()

    @Provides
    fun proporcionarEventoDao(baseDatos: PetCardDatabase): EventoDao =
        baseDatos.eventoDao()
}
