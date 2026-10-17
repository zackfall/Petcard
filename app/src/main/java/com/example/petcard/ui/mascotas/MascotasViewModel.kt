package com.example.petcard.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.data.local.MascotaEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// MascotasViewModel observa la base de datos y convierte cada cambio en un estado para la pantalla, y su única acción es eliminar,
// que borra de la base de datos y deja que el cambio llegue solo a la pantalla.
// Es exactamente lo mismo que vimos al inicio: Hilt crea este ViewModel y le entrega el PetRepository.
@HiltViewModel
class MascotasViewModel @Inject constructor(
    private val repositorio: PetRepository,
) : ViewModel() {

    // Aquí no hay un MutableStateFlow que el ViewModel modifique a mano (como en el formulario). En cambio,
    // el estado se deriva de la base de datos. Se construye en tres pasos encadenados:
    // repositorio.observarMascotas()= Devuelve un Flow<List<MascotaEntity>>. Es un flujo que emite la lista completa de mascotas cada vez que algo cambia en la base de
    // datos (si se agrega, edita o elimina una). Esto es típico de Room: si tu DAO devuelve un Flow, Room avisa solo cuando la tabla cambia.
    val estado: StateFlow<MascotasUiState> = repositorio.observarMascotas()
        // .map { MascotasUiState(mascotas = it, cargando = false) }= Transforma cada lista que llega en un MascotasUiState. Ese it es la lista de mascotas.
        // Se marca cargando = false porque, si ya llegó una lista, es que los datos ya cargaron.
        .map { MascotasUiState(mascotas = it, cargando = false) }
        // .stateIn(...)= Convierte ese Flow normal en un StateFlow, que es lo que la pantalla necesita para collectAsStateWithLifecycle(). Tiene tres parámetros:
        // viewModelScope: el flujo vive mientras viva el ViewModel.
        // SharingStarted.WhileSubscribed(5_000): empieza a escuchar la base de datos solo cuando alguien (la pantalla) está observando, y si nadie observa durante
        // 5 segundos, deja de escuchar. Esos 5 segundos evitan reiniciar todo si el usuario solo rota el celular (la pantalla se desconecta un instante y vuelve).
        // MascotasUiState(): el valor inicial, antes de que llegue algo de la base de datos. Como el estado por defecto seguramente tiene cargando = true y lista vacía,
        // la pantalla puede mostrar “cargando” al principio. Por eso en MascotasContent existía la condición !estado.cargando antes del mensaje de lista vacía.
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MascotasUiState())

    fun eliminar(mascota: MascotaEntity) {
        viewModelScope.launch { repositorio.eliminarMascota(mascota) }
    }
}