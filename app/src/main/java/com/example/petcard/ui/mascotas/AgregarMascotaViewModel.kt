package com.example.petcard.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity
import com.example.petcard.util.Dates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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

    private fun cambiar(transformacion: (AgregarMascotaUiState) -> AgregarMascotaUiState) {
        formulario.value = transformacion(formulario.value).copy(error = null)
    }

    fun onNombreChange(valor: String) = cambiar { it.copy(nombre = valor) }

    fun onEspecieChange(valor: String) = cambiar { it.copy(especie = valor) }

    fun onRazaChange(valor: String) = cambiar { it.copy(raza = valor) }

    fun onEdadChange(valor: String) =
        cambiar { it.copy(edadAnos = valor.filter { c -> c.isDigit() }) }

    fun onPesoChange(valor: String) = cambiar { it.copy(pesoKg = formatearPeso(valor)) }

    fun onFotoChange(uri: String?) = cambiar { it.copy(fotoUri = uri) }

    fun onGuardar() {
        val actual = formulario.value
        if (!actual.puedeGuardar) return
        formulario.value = actual.copy(guardando = true, error = null)
        viewModelScope.launch {
            try {
                val id = repositorio.guardarMascota(
                    MascotaEntity(
                        nombre = actual.nombre.trim(),
                        especie = actual.especie.trim(),
                        raza = actual.raza.trim(),
                        fechaNacimientoMillis = nacimientoPorEdad(actual.edadAnosInt!!),
                        fotoUri = actual.fotoUri,
                        pesoKg = actual.pesoKgDouble,
                    ),
                )
                formulario.value = formulario.value.copy(guardando = false)
                _eventos.send(AgregarMascotaEvent.Guardado(id))
            } catch (cancelado: CancellationException) {
                throw cancelado
            } catch (fallo: Exception) {
                formulario.value = formulario.value.copy(
                    guardando = false,
                    error = "No se pudo guardar la mascota. Inténtalo de nuevo.",
                )
            }
        }
    }
}

/**
 * Convierte la edad del formulario en la fecha de nacimiento que exige Room.
 * Si hoy es 29 de febrero y el año calculado no es bisiesto, se usa el 28:
 * con un 29/2 inexistente `Calendar` (lenient) rueda a 1 de marzo y la edad
 * mostrada después quedaría un año menor.
 */
private fun nacimientoPorEdad(edadAnos: Int): Long {
    val hoy = Calendar.getInstance()
    val anio = hoy.get(Calendar.YEAR) - edadAnos
    val mes = hoy.get(Calendar.MONTH) + 1
    val dia = hoy.get(Calendar.DAY_OF_MONTH)
    val diaSeguro = if (mes == 2 && dia == 29 && !Dates.esBisiesto(anio)) 28 else dia
    return Dates.inicioDiaMillis(anio, mes, diaSeguro)
}
