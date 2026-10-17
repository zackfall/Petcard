package com.example.petcard.ui.mascotas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcard.data.PetRepository
import com.example.petcard.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Perfil de una mascota concreta: su `mascotaId` llega de la ruta
 * `perfil_mascota/{mascotaId}` vía [SavedStateHandle] (lo rellena NavHost).
 * Lee la mascota y su historial con Flow del repositorio (Room = fuente única).
 */

//PerfilMascotaViewModel lee el id de la mascota desde los argumentos de navegación, combina el flujo de la mascota con el de sus eventos, ordena los eventos del
// más reciente al más antiguo y publica el resultado como un StateFlow de solo lectura.

//PetRepository: para leer los datos, como en los otros ViewModels.
//SavedStateHandle: un “cuaderno” que Hilt rellena con los argumentos de la navegación. Fíjate que no tiene private val:
// solo se usa en el constructor para leer el id y no hace falta guardarlo como propiedad.
@HiltViewModel
class PerfilMascotaViewModel @Inject constructor(
    private val repositorio: PetRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    //Aquí se resuelve la duda de PerfilMascotaRoute. Cuando navegas a algo como "perfil/5", el 5 queda guardado en SavedStateHandle bajo el nombre Routes.ARG_MASCOTA_ID (una constante de tu archivo de rutas).
    //
    //savedStateHandle[...]: lee el argumento.
    //?: -1L: si no existe, usa -1. Ninguna mascota real tiene ese id (los ids autogenerados empiezan en 1), así que las consultas devolverían null
    // y la pantalla mostraría “No se encontró la mascota.” en lugar de cerrarse con un error.
    private val mascotaId: Long = savedStateHandle[Routes.ARG_MASCOTA_ID] ?: -1L

    //Aquí hay dos flujos de la base de datos y una sola pantalla que necesita ambos:
    //
    //observarMascota(mascotaId) → Flow<MascotaEntity?>
    //observarEventosDeMascota(mascotaId) → Flow<List<EventoEntity>>
    //
    //combine los junta: cada vez que cualquiera de los dos emite algo nuevo, ejecuta el bloque con el último valor de cada uno y produce un estado nuevo.
    //Si editas el nombre de la mascota → emite el primer flujo → el perfil se actualiza.
    //Si agregas un evento → emite el segundo flujo → aparece en el historial.
    val uiState: StateFlow<PerfilMascotaUiState> = combine(
        repositorio.observarMascota(mascotaId),
        repositorio.observarEventosDeMascota(mascotaId),
    ) { mascota, eventos ->

        //cargando = false: si llegó el primer resultado, ya cargó.
        //mascota = mascota: puede ser null si no existe, y de ahí sale encontrada = false.
        //sortedByDescending { it.fechaMillis }: ordena los eventos del más reciente al más antiguo. Por eso el historial muestra arriba lo último que pasó.
        // Nota que el orden se hace aquí y no en la consulta SQL.
        PerfilMascotaUiState(
            cargando = false,
            mascota = mascota,
            eventos = eventos.sortedByDescending { it.fechaMillis },
        )

        //viewModelScope: vive mientras viva el ViewModel.
        //WhileSubscribed(5_000): solo escucha la base de datos mientras alguien observa, y espera 5 segundos antes de parar (para sobrevivir a una rotación de pantalla).
        //PerfilMascotaUiState(): el valor inicial, con cargando = true.
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilMascotaUiState())
}
