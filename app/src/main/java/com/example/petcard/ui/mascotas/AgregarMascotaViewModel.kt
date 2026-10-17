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

//Es el aviso de un solo uso que va por el Channel. sealed interface significa que solo existen los eventos listados aquí, lo que permite al when de la pantalla
// saber que cubrió todos los casos. Por ahora hay uno solo: Guardado, con el id de la mascota.
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

    // Recibe una función (transformacion) que dice cómo cambiar el estado.
    //La aplica al estado actual: transformacion(formulario.value).
    //Borra el error con .copy(error = null).
    // sirve para actualizar el formulario sin repetir código y, de paso, quitar el mensaje de error en cuanto el usuario modifica cualquier campo.
    private fun cambiar(transformacion: (AgregarMascotaUiState) -> AgregarMascotaUiState) {
        formulario.value = transformacion(formulario.value).copy(error = null)
    }

    // { it.copy(nombre = valor) } es la transformación: “toma el estado (it) y devuelve una copia con el nombre cambiado”.
    //Se usa = en lugar de { } porque la función es de una sola expresión.
    // Cada vez que el usuario escribe, se crea una copia nueva del estado con el campo cambiado y esa copia reemplaza a la anterior,
    // así StateFlow detecta el cambio y la pantalla se actualiza.
    fun onNombreChange(valor: String) = cambiar { it.copy(nombre = valor) }

    fun onEspecieChange(valor: String) = cambiar { it.copy(especie = valor) }

    fun onRazaChange(valor: String) = cambiar { it.copy(raza = valor) }

    // El usuario escribe "3a" en el campo de edad:
    //
    //Se llama onEdadChange("3a").
    //valor.filter { c -> c.isDigit() } convierte "3a" en "3".
    //it.copy(edadAnos = "3") crea una copia del estado con la edad "3".
    //cambiar guarda esa copia y borra el error.
    //La pantalla se redibuja mostrando 3, sin la a.
    fun onEdadChange(valor: String) =
        cambiar { it.copy(edadAnos = valor.filter { c -> c.isDigit() }) }

    fun onPesoChange(valor: String) = cambiar { it.copy(pesoKg = formatearPeso(valor)) }

    fun onFotoChange(uri: String?) = cambiar { it.copy(fotoUri = uri) }

    fun onGuardar() {
        // toma una foto del estado, se sale si el formulario no es válido, y marca guardando = true. Lo único nuevo es que también limpia error.
        val actual = formulario.value
        if (!actual.puedeGuardar) return
        formulario.value = actual.copy(guardando = true, error = null)
        viewModelScope.launch {
            // try significa “intenta esto”, y los catch dicen “si algo falla, haz esto otro”.

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
