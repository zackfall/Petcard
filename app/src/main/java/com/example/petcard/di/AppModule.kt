package com.example.petcard.di

import android.content.Context
import com.example.petcard.data.PetRepository
import com.example.petcard.data.PetRepositoryImpl
import com.example.petcard.data.local.EventoDao
import com.example.petcard.data.local.MascotaDao
import com.example.petcard.data.local.PetCardDatabase
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Grafo de dependencias con Hilt (reemplaza al AppContainer manual).
 *
 * REGLA DE EQUIPO: ningún ViewModel instancia la BD ni el repositorio.
 * Todos los ViewModels reciben [PetRepository] por constructor vía
 * `@HiltViewModel` + `@Inject` y se crean con `hiltViewModel()`.
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

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun enlazarRepositorio(implementacion: PetRepositoryImpl): PetRepository
}
