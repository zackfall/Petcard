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


//@HiltViewModel= Es una anotación que le dice a Hilt: “esta clase es un ViewModel y tú te encargas de crearla y darle lo que necesite”.
// Sin esto, Hilt no sabría cómo construirla.
// @Inject constructor(...)= Le indica a Hilt: “usa este constructor para crear la clase, y rellena los parámetros por ti”. Esto se llama inyección de dependencias.
// : ViewModel()= Significa que la clase hereda de ViewModel, la clase base de Android que da el ciclo de vida correcto.
@HiltViewModel
class AgregarMascotaViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {

    //private val formulario= Es privada porque solo el ViewModel debe poder modificar el estado.
    // MutableStateFlow(AgregarMascotaUiState())= Es una “caja” que guarda un valor y avisa a quien la observe cada vez que cambia.
    //val uiState: StateFlow<...> = formulario.asStateFlow()= Es la versión de solo lectura de la misma caja. La pantalla puede observarla,
    // pero no cambiarla. Así evitas que la UI modifique el estado por su cuenta.
    private val formulario = MutableStateFlow(AgregarMascotaUiState())
    val uiState: StateFlow<AgregarMascotaUiState> = formulario.asStateFlow()


    // Channel<AgregarMascotaEvent>(Channel.BUFFERED)= Un Channel es como un buzón: el ViewModel deja eventos y la pantalla los recoge.
    // Con BUFFERED el buzón tiene espacio para guardar varios eventos si la pantalla no los está recibiendo justo en ese momento, para que no se pierdan.
    // receiveAsFlow()= Convierte el buzón en un Flow que la pantalla puede escuchar. Cada evento se entrega una sola vez: una vez consumido, desaparece.
    private val _eventos = Channel<AgregarMascotaEvent>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    //Cada vez que el usuario escribe en un campo (nombre, especie, raza), la pantalla llama a esta función con el texto nuevo.
    //En esta función entra lo que es el "AgregarMascotaUiState" lo que hace es crear una copia para no modificarlo directamente,
    // pero con el campo cambiado y esta copia se asigna a formulario.value, como el valor cambio StateFlow avisa a la pantalla y lo redibuja con el texto nuevo
    fun onNombreChange(valor: String) {
        formulario.value = formulario.value.copy(nombre = valor)
    }

    fun onEspecieChange(valor: String) {
        formulario.value = formulario.value.copy(especie = valor)
    }

    fun onRazaChange(valor: String) {
        formulario.value = formulario.value.copy(raza = valor)
    }

    //El valor.filter { it.isDigit() }= se queda solo con los dígitos. Si el usuario escribe "3a", el estado guarda "3".
    // Así el campo de edad nunca contiene letras ni símbolos, aunque el teclado lo permita.
    fun onEdadChange(valor: String) {
        formulario.value = formulario.value.copy(edadAnos = valor.filter { it.isDigit() })
    }

    //Guarda la ruta (URI) de la foto elegida. Es String? porque puede ser null si no hay foto o el usuario la quita.
    fun onFotoChange(uri: String?) {
        formulario.value = formulario.value.copy(fotoUri = uri)
    }

    fun onGuardar() {
        //Toma una “foto” del estado actual. Si el formulario no es válido (por ejemplo, falta el nombre),
        // puedeGuardar es false y la función termina sin hacer nada. Es una protección extra, por si el botón se pulsa estando deshabilitado.
        val actual = formulario.value
        if (!actual.puedeGuardar) return
        // Marca que se está guardando. La pantalla puede usar eso para mostrar un indicador de carga y bloquear el botón, así el usuario no toca “Guardar” dos veces.
        formulario.value = actual.copy(guardando = true)
        //Lanza una corrutina, porque guardar en base de datos es una operación que no debe bloquear la pantalla.
        // Además, viewModelScope la cancela sola si el ViewModel se destruye.
        viewModelScope.launch {

            //guarda y devuelve el id generado.
            val id = repositorio.guardarMascota(
                MascotaEntity(
                    nombre = actual.nombre.trim(), //.trim() quita espacios al inicio y al final.
                    especie = actual.especie.trim(),
                    raza = actual.raza.trim(),
                    //nacimientoPorEdad(...) convierte la edad en años a una fecha de nacimiento aproximada (en milisegundos).
                    // Se guarda la fecha y no la edad porque la edad cambia con el tiempo, pero la fecha de nacimiento no.
                    fechaNacimientoMillis = nacimientoPorEdad(actual.edadAnosInt!!),
                    fotoUri = actual.fotoUri,
                ),
            )
            _eventos.send(AgregarMascotaEvent.Guardado(id))
        }
    }
}

/** Convierte la edad del formulario en la fecha de nacimiento que exige Room. */

//Recibe la edad y devuelve un Long, que es la fecha en milisegundos. Es private porque solo la usa este ViewModel.
private fun nacimientoPorEdad(edadAnos: Int): Long {
    //Obtiene la fecha y hora actuales del dispositivo.
    val hoy = Calendar.getInstance()
    return Dates.inicioDiaMillis(
        //Calcula el año de nacimiento: año actual menos la edad. Si hoy es 2026 y la mascota tiene 3 años, da 2023.
        hoy.get(Calendar.YEAR) - edadAnos,
        hoy.get(Calendar.MONTH) + 1,
        hoy.get(Calendar.DAY_OF_MONTH),
    )
}
