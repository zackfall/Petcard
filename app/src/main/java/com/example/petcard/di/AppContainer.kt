package com.example.petcard.di

import android.content.Context
import com.example.petcard.data.PetRepository
import com.example.petcard.data.PetRepositoryImpl
import com.example.petcard.data.local.PetCardDatabase

/**
 * Contenedor manual de dependencias (DI sin Hilt).
 *
 * REGLA DE EQUIPO: ningún ViewModel instancia la BD ni el repositorio.
 * Todos los ViewModels reciben [PetRepository] por constructor y se crean
 * con su `Factory` (ver cada `*ViewModel.Factory`).
 */
interface AppContainer {
    val petRepository: PetRepository
}

class DefaultAppContainer(contexto: Context) : AppContainer {
    private val baseDatos: PetCardDatabase by lazy {
        PetCardDatabase.obtener(contexto)
    }

    override val petRepository: PetRepository by lazy {
        PetRepositoryImpl(
            mascotaDao = baseDatos.mascotaDao(),
            eventoDao = baseDatos.eventoDao(),
        )
    }
}
