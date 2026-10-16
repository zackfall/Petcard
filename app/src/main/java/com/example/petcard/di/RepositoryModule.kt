package com.example.petcard.di

import com.example.petcard.data.AjustesRepository
import com.example.petcard.data.AjustesRepositoryImpl
import com.example.petcard.data.PetRepository
import com.example.petcard.data.PetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Enlaza la interfaz [PetRepository] con su implementación.
 * Los ViewModels solo conocen la interfaz (rúbrica: Patrón Repositorio).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun enlazarRepositorio(implementacion: PetRepositoryImpl): PetRepository

    @Binds
    @Singleton
    abstract fun enlazarAjustes(implementacion: AjustesRepositoryImpl): AjustesRepository
}
