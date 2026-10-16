package com.example.petcard.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persiste las preferencias en `SharedPreferences` y las expone como
 * [StateFlow] para que la UI (tema) reaccione en vivo.
 */
@Singleton
class AjustesRepositoryImpl @Inject constructor(
    @ApplicationContext contexto: Context,
) : AjustesRepository {

    private val prefs = contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    private val _modoOscuro = MutableStateFlow(
        if (prefs.contains(CLAVE_MODO_OSCURO)) prefs.getBoolean(CLAVE_MODO_OSCURO, false) else null,
    )

    override val modoOscuro: StateFlow<Boolean?> = _modoOscuro.asStateFlow()

    override suspend fun guardarModoOscuro(valor: Boolean) {
        prefs.edit().putBoolean(CLAVE_MODO_OSCURO, valor).apply()
        _modoOscuro.value = valor
    }

    private companion object {
        const val ARCHIVO = "petcard_ajustes"
        const val CLAVE_MODO_OSCURO = "modoOscuro"
    }
}
