package com.example.petcard.ui.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petcard.PetCardApplication
import com.example.petcard.ui.calendario.CalendarioRoute
import com.example.petcard.ui.eventos.EventosPendientesRoute
import com.example.petcard.ui.eventos.NuevoEventoRoute
import com.example.petcard.ui.navigation.Routes
import com.example.petcard.ui.inicio.InicioRoute
import com.example.petcard.ui.mascotas.MascotasRoute

private val destinosPrincipales = listOf(
    DestinoBottomBar(Routes.INICIO, "Inicio", Icons.Filled.Home),
    DestinoBottomBar(Routes.MASCOTAS, "Mascotas", Icons.Filled.Pets),
    DestinoBottomBar(Routes.EVENTOS_PENDIENTES, "Salud", Icons.Filled.MonitorHeart),
    DestinoBottomBar(Routes.CALENDARIO, "Calendario", Icons.Filled.CalendarMonth),
)

private fun tituloPara(ruta: String?): String = when (ruta) {
    Routes.INICIO -> "PetCard"
    Routes.MASCOTAS -> "Mascotas"
    Routes.AGREGAR_MASCOTA -> "Agregar Mascota"
    Routes.NUEVO_EVENTO -> "Nuevo Evento"
    Routes.CALENDARIO -> "Calendario"
    Routes.EVENTOS_PENDIENTES -> "Evento de Salud"
    Routes.CONFIGURACION -> "Configuración"
    Routes.PRIVACIDAD -> "Privacidad de datos"
    else -> if (ruta?.startsWith("perfil_mascota/") == true) "Perfil de Mascota" else "PetCard"
}

private val rutasRaiz = setOf(
    Routes.INICIO,
    Routes.MASCOTAS,
    Routes.EVENTOS_PENDIENTES,
    Routes.CALENDARIO,
)

/** Las rutas raíz no muestran flecha atrás; el resto sí (ver diseño). */
private fun esRutaRaiz(ruta: String?): Boolean = ruta in rutasRaiz

/**
 * Shell de la app (dueño: responsable de TopBar + BottomBar + navegación).
 * Los integrantes conectan sus pantallas en el [NavHost] sin modificar
 * el resto de este archivo: reemplazan el `PlaceholderScreen` de su ruta
 * por su `*Route` real.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetCardScaffold(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(tituloPara(rutaActual)) },
                navigationIcon = {
                    if (!esRutaRaiz(rutaActual)) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    // Diseño: en el home van campana (pendientes) y ajustes.
                    if (rutaActual == Routes.INICIO) {
                        IconButton(
                            onClick = { navController.navigate(Routes.EVENTOS_PENDIENTES) },
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Recordatorios")
                        }
                        IconButton(
                            onClick = { navController.navigate(Routes.CONFIGURACION) },
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Configuración")
                        }
                    }
                    // Diseño: en Evento de Salud va "+" para crear.
                    if (rutaActual == Routes.EVENTOS_PENDIENTES) {
                        IconButton(
                            onClick = { navController.navigate(Routes.NUEVO_EVENTO) },
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Nuevo evento")
                        }
                    }
                },
            )
        },
        bottomBar = {
            PetCardBottomBar(
                destinos = destinosPrincipales,
                rutaActual = rutaActual,
                alNavegar = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { padding ->
        val app = LocalContext.current.applicationContext as PetCardApplication
        val repositorio = app.container.petRepository

        NavHost(
            navController = navController,
            startDestination = Routes.INICIO,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.INICIO) {
                InicioRoute(
                    repositorio = repositorio,
                    alVerTodos = { navController.navigate(Routes.EVENTOS_PENDIENTES) },
                )
            }
            composable(Routes.MASCOTAS) {
                MascotasRoute(
                    repositorio = repositorio,
                    alAgregarMascota = { navController.navigate(Routes.AGREGAR_MASCOTA) },
                    alEditar = { /* pendiente: perfil/edición */ },
                    alNuevoEvento = { navController.navigate(Routes.NUEVO_EVENTO) },
                )
            }
            composable(
                route = Routes.PERFIL_MASCOTA,
                arguments = listOf(navArgument(Routes.ARG_MASCOTA_ID) { type = NavType.LongType }),
            ) {
                PlaceholderScreen(nombre = "Perfil de Mascota")
            }
            composable(Routes.AGREGAR_MASCOTA) {
                PlaceholderScreen(nombre = "Agregar Mascota")
            }
            composable(Routes.NUEVO_EVENTO) {
                NuevoEventoRoute(
                    repositorio = repositorio,
                    alGuardar = { navController.popBackStack() },
                )
            }
            composable(Routes.CALENDARIO) {
                CalendarioRoute(repositorio = repositorio)
            }
            composable(Routes.EVENTOS_PENDIENTES) {
                EventosPendientesRoute(
                    repositorio = repositorio,
                    alCrearEvento = { navController.navigate(Routes.NUEVO_EVENTO) },
                )
            }
            composable(Routes.CONFIGURACION) {
                PlaceholderScreen(nombre = "Configuración")
            }
            composable(Routes.PRIVACIDAD) {
                PlaceholderScreen(nombre = "Privacidad de datos")
            }
        }
    }
}

// Los ViewModels se crean dentro de cada *Route con su Factory;
// el shell solo provee el repositorio del AppContainer.
