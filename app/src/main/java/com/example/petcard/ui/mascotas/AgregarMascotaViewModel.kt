package com.example.petcard.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.util.Dates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/** Eventos puntuales (one-off): no van en el StateFlow. */
sealed interface AgregarMascotaEvent {
    /** La mascota ya quedó en Room: la Route navega atrás. */
    data class Guardado(val mascotaId: Long) : AgregarMascotaEvent
}

@HiltViewModel
class AgregarMascotaViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {

    private val formulario = MutableStateFlow(AgregarMascotaUiState())
    val uiState: StateFlow<AgregarMascotaUiState> = formulario.asStateFlow()

    private val _eventos = Channel<AgregarMascotaEvent>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    fun onNombreChange(valor: String) {
        formulario.value = formulario.value.copy(nombre = valor)
    }

    fun onEspecieChange(valor: String) {
        formulario.value = formulario.value.copy(especie = valor)
    }

    fun onRazaChange(valor: String) {
        formulario.value = formulario.value.copy(raza = valor)
    }

    fun onEdadChange(valor: String) {
        formulario.value = formulario.value.copy(edadAnos = valor.filter { it.isDigit() })
    }

    fun onFotoChange(uri: String?) {
        formulario.value = formulario.value.copy(fotoUri = uri)
    }

    fun onGuardar() {
        val actual = formulario.value
        if (!actual.puedeGuardar) return
        formulario.value = actual.copy(guardando = true)
        viewModelScope.launch {
            val id = repositorio.guardarMascota(
                MascotaEntity(
                    nombre = actual.nombre.trim(),
                    especie = actual.especie.trim(),
                    raza = actual.raza.trim(),
                    fechaNacimientoMillis = nacimientoPorEdad(actual.edadAnosInt!!),
                    fotoUri = actual.fotoUri,
                ),
            )
            _eventos.send(AgregarMascotaEvent.Guardado(id))
        }
    }
}

/** Convierte la edad del formulario en la fecha de nacimiento que exige Room. */
private fun nacimientoPorEdad(edadAnos: Int): Long {
    val hoy = Calendar.getInstance()
    return Dates.inicioDiaMillis(
        hoy.get(Calendar.YEAR) - edadAnos,
        hoy.get(Calendar.MONTH) + 1,
        hoy.get(Calendar.DAY_OF_MONTH),
    )
}
