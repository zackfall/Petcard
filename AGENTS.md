# PetCard — Reglas del equipo (leer antes de programar)

App offline-first de carné de salud para mascotas. Kotlin + Jetpack Compose + Material 3.
Rúbrica oficial en `diseno/Trabajo Autónomo - 0.5 punto - Elaboración de app móvil nativa.pdf` — todo lo de abajo existe para cumplirla.

## Arquitectura obligatoria (la evalúan)

MVVM + Room como única fuente de verdad + repositorio + DI manual:

```
data/local/      → Room: *Entity, *Dao (lecturas Flow<T>, escrituras suspend fun), PetCardDatabase
data/            → PetRepository (interfaz) + PetRepositoryImpl
di/              → AppContainer / DefaultAppContainer (DI manual, NO Hilt)
notifications/   → Notificaciones (canal + AlarmManager) + RecordatorioReceiver (dueño: shell-owner)
ui/navigation/   → Routes (rutas congeladas)
ui/shell/        → PetCardScaffold (TopBar + BottomBar + NavHost), PetCardBottomBar, PlaceholderScreen
ui/shared/       → EventoUi.kt (TipoEvento.etiqueta(), EventoRow, EventoItem, lineaEvento())
ui/eventos/      → pantallas Nuevo Evento + Eventos Pendientes (OCUPADAS, ver dueños)
ui/calendario/   → pantalla Calendario (OCUPADA, ver dueños)
util/            → Dates (fechas como Long = inicio del día local; horas como Int = min. desde medianoche)
```

Fechas: **prohibido `java.time`** (minSdk 24). Usar `Dates.inicioDiaMillis()`, `Dates.formatearFecha()`, `Dates.formatearHora()`.

## Patrón Route / Content (rúbrica 2.5 pts, regla estricta del docente)

Cada pantalla = 3 archivos. Ejemplo real: `ui/eventos/NuevoEvento{ViewModel,Route,Content}.kt`.

- `*ViewModel` — expone UN solo `StateFlow<UiState>` (+ `Channel` para eventos puntuales como navegación). Recibe `PetRepository` por constructor, se crea con `SuViewModel.factory(repo)`. Lógica de guardado en `viewModelScope`. Jamás instancia la BD.
- `*Route` (stateful) — crea el VM con `viewModel(factory = ...)`, consume con `collectAsStateWithLifecycle()`, traduce eventos puntuales (ej. navegar atrás). Pasa **solo estado inmutable + lambdas** al Content.
- `*Content` (stateless) — **PROHIBIDO recibir el ViewModel o el repositorio**. Solo datos + lambdas `() -> Unit`. Debe tener `@Preview`.

```kotlin
// ✅ BIEN
MiPantallaContent(estado = estado, onGuardar = vm::onGuardar)
// ❌ MAL — resta puntos: pasar el ViewModel a hijos
MiPantallaContent(viewModel = vm)
```

## Contratos congelados (no renombrar sin acuerdo del equipo)

- Rutas (`ui/navigation/Routes.kt`): `inicio, mascotas, perfil_mascota/{mascotaId}, agregar_mascota, nuevo_evento, calendario, eventos_pendientes, configuracion, privacidad`. Navegación solo vía `NavHost` del shell, argumentos con `navArgument` (ver `PERFIL_MASCOTA`).
- Datos: `MascotaEntity(id, nombre, especie, raza, fechaNacimientoMillis, fotoUri, pesoKg)`, `EventoEntity(id, mascotaId, tipo, titulo, fechaMillis, horaMinutos?, lugar?, notas?, proximaMillis?, hecho)`, `TipoEvento(VACUNA, DESPARASITACION, CONTROL_VETERINARIO, MEDICACION, BANO, OTRO)`. Nueva columna Room = nueva versión de BD + migración (coordinar con el equipo).
- Acceso a datos solo vía `PetRepository`. El ViewModel jamás toca un DAO.
- Recordatorios: `EventoEntity.proximaMillis` + `Notificaciones.programar/cancelar` (AlarmManager). La Route traduce el evento puntual del VM en programar/cancelar; el VM jamás toca `Context`. Permiso `POST_NOTIFICATIONS` se pide en `MainActivity`.
- TopBar (acciones del dueño del shell): flecha atrás en rutas no-raíz; en `inicio` van campana → `eventos_pendientes` y ajustes → `configuracion`; en `eventos_pendientes` va `+` → `nuevo_evento`.
- Etiquetas de tipo y filas de evento: reutilizar `ui/shared/EventoUi.kt`, no duplicar formatos.

## Dueños y ramas (un PR por rama hacia `main`)

| Pantalla (diseño) | Dueño | Rama | Carpeta destino |
|---|---|---|---|
| Shell: TopBar + BottomBar + NavHost + theme | shell-owner | `feature/app-shell-barras-navegacion` | `ui/shell/`, `ui/navigation/`, `ui/theme/`, `data/`, `di/` |
| Inicio / Salud (`screen-container.pdf`) | teammate | `feature/pantalla-inicio-salud` | `ui/inicio/` |
| Lista mascotas (`screen-container-1.pdf`) | teammate | `feature/lista-mascotas` | `ui/mascotas/` |
| Perfil mascota (`screen-container-2.pdf`) | teammate | `feature/perfil-mascota` | `ui/mascotas/` |
| Agregar mascota (`screen-container-3.pdf`) | teammate | `feature/formulario-agregar-mascota` | `ui/mascotas/` |
| Nuevo evento (`screen-container-4.pdf`) | screen-owner | `feature/formulario-nuevo-evento` | `ui/eventos/` ✅ hecha |
| Calendario (`screen-container-5.pdf`) | screen-owner | `feature/calendario-mensual` | `ui/calendario/` ✅ hecha |
| Eventos pendientes (`screen-container-6.pdf`) | screen-owner | `feature/lista-eventos-pendientes` | `ui/eventos/` ✅ hecha |
| Configuración (`screen-container-7.pdf`) | teammate | `feature/pantalla-configuracion` | `ui/config/` |
| Privacidad (`screen-container-8.pdf`) | teammate | `feature/pantalla-privacidad` | `ui/config/` |

Convención: `feature/<qué-hace-la-pantalla>` (intuitivo, sin números de screen).

## Zonas prohibidas por rama

- Solo `feature/app-shell-barras-navegacion` toca: `MainActivity.kt`, `PetCardApplication.kt`, `AndroidManifest.xml`, `ui/shell/`, `ui/navigation/`, `ui/theme/`, `data/`, `di/`, `gradle/libs.versions.toml`, `*/build.gradle.kts`.
- Las ramas de pantalla **solo añaden archivos en su carpeta** `ui/<area>/` y reemplazan su `PlaceholderScreen` por su `*Route` en el `NavHost` (una línea por pantalla).
- ¿Necesitas un widget compartido? No lo dupliques: proponlo al dueño del shell para `ui/shared/`.

## Checklist del PR (contra la rúbrica)

1. Compose + Material 3, sin vistas XML. [UI 2.5]
2. Ningún `@Composable` hijo recibe ViewModel; estado vía `collectAsStateWithLifecycle()`. [UI 2.5]
3. Su ViewModel expone un único `StateFlow<UiState>`; escrituras en `viewModelScope`. [MVVM 2.0]
4. Lecturas vía `Flow` del repositorio; escrituras con `suspend fun`; nada de I/O en hilo principal. [Room 2.5]
5. ViewModel depende de `PetRepository` (interfaz), creado con `Factory`. [Repo 1.5 + DI 1.5]
6. Navegación declarada en el `NavHost` central, sin rutas hardcodeadas sueltas. [Nav 1.5]
7. `@Preview` en cada `*Content`.
8. Cada integrante debe tener **commits visibles** en el repo nube (sin commits = 0 en la nota grupal).

## Comandos

```bash
./gradlew assembleDebug   # compilar
./gradlew installDebug    # instalar en emulador/dispositivo
./gradlew test            # unit tests
```

Demo obligatoria (ensayarla): registrar dato → matar la app → modo avión → el dato sigue y la UI reacciona (Room `Flow`).
