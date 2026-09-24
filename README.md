# 🐾 PetCard — Carné de Vacunación y Control de Salud Digital para Mascotas

> PetCard es una aplicación móvil que funciona como un carné de vacunación y control de salud digital para mascotas, permitiendo al dueño registrar y consultar de forma rápida y sin conexión a internet el historial de vacunas, desparasitaciones y controles veterinarios de su mascota.

Proyecto final de la asignatura **Aplicaciones Móviles**.

---

## 📚 Contexto académico

| Campo | Detalle |
|-------|---------|
| **Universidad** | Universidad Laica Eloy Alfaro de Manabí |
| **Facultad** | Ciencias Informáticas |
| **Carrera** | Tecnología de la Información |
| **Asignatura** | Aplicaciones Móviles |
| **Docente** | Joffre Edgardo Panchana Flores |
| **Fecha definición** | 6 de septiembre de 2026 |
| **Tema** | Definición general del proyecto final de asignatura — PetCard |

### 👥 Integrantes

- Eduardo Josue López López
- Castillo Loor José Manuel
- Reyes Peñaherrera Pierina Natalia
- Ruben Isaac Zamora Reyes

---

## 💡 Descripción y temática

### Necesidad detectada

En la actualidad el seguimiento de la salud de las mascotas se realiza en su mayoría de forma manual: carnés de papel que se pierden o deterioran, recordatorios anotados en agendas físicas, o simplemente la memoria del dueño.

Esto genera problemas comunes:

- Olvido de fechas de vacunación.
- Pérdida del historial médico al cambiar de veterinario.
- Dificultad para tener a mano la información de la mascota en caso de emergencia.

### Temática escogida

Aplicación móvil orientada al **cuidado y seguimiento de mascotas**, que centraliza en un solo lugar el registro de eventos de salud:

- Vacunas
- Desparasitaciones
- Controles veterinarios
- Baños
- Medicación

asociados a cada mascota del usuario, con un historial ordenado cronológicamente, fácil de consultar y siempre disponible desde el teléfono.

### Enfoque del producto

- **Usuario único:** el dueño de la mascota. Sin roles, sin invitaciones, sin cuentas.
- **Multi-mascota:** gestiona una o varias mascotas en la misma app.
- **100% offline-first:** toda la información se guarda en el dispositivo, sin depender de internet ni servidores externos.
- **Simple y accesible:** pensada para personas con poco conocimiento tecnológico. Registrar un evento debe tomar menos de un minuto.

### Criterios clave de elección

1. **Simplicidad del dominio:** los datos de una mascota (identificación, historial de salud y tipos de eventos) son fáciles de estructurar en una base de datos y de representar visualmente.
2. **Relevancia práctica:** el cuidado y seguimiento de la salud de una mascota es una actividad común en gran parte de las familias.
3. **Diseño Centrado en el Usuario (DCU):** se trata de un problema cotidiano y fácilmente observable.

---

## 🔍 Benchmarking

Análisis de apps similares en tiendas de aplicaciones:

| App | Puntos fuertes | Puntos débiles vs. PetCard |
|-----|----------------|----------------------------|
| **Medika – Carnet Salud Mascota** | • Registro de múltiples especies (hámsters, conejos, etc.).<br>• Seguimiento continuo de peso.<br>• Alertas de vacunas y tratamientos en curso.<br>• Compartir información médica con la familia. | • Interfaz genérica, sin identidad visual tipo carné.<br>• Requiere cuenta / sincronización con familia y conexión a internet. |
| **Petic: Carnet de Mascotas** | • Registro de vacunas por nombre exacto (ej. FIP, Rabia).<br>• Cubre vacunas, citas y medicamentos en un solo sistema de alertas.<br>• Historial clínico que distingue notas del dueño vs. registros oficiales de la clínica. | • Requiere sincronización en tiempo real (internet + backend).<br>• Sistema de roles (dueño, cuidador, clínica) innecesariamente complejo para quien solo quiere anotar una vacuna rápido. |

**Conclusión:** ambas apps son robustas, pero dependen de conexión, cuentas o terceros (familia / clínica) para su valor completo. **PetCard se diferencia al ser 100% local, de un solo usuario, sin configurar roles ni depender de internet**, más simple, rápida y accesible.

---

## 👤 Investigación de usuarios — Personas

### Ficha 1: Persona focal — “La dueña ocupada”

- **Nombre:** Laura López, 40 años, Ingeniera Comercial
- **Mascotas:** 3 perros — Pinky (7), Lazy (5), Doki (1)
- **Perfil:** rutina laboral demandante, poco conocimiento/interés técnico, hoy usa notas del teléfono o depende de que la veterinaria le avise. Le genera ansiedad pasar por alto citas.
- **Necesidades:** recordatorios automáticos claros y con anticipación, registro en <1 minuto, sin crear cuentas ni sincronizaciones.
- **Escenario:** domingo por la noche recibe notificación *“Lazy necesita su desparasitación en 3 días”*, revisa la línea de tiempo, confirma que pasó un mes desde la última dosis y tras la visita registra el evento seleccionando el icono, fecha y guardar.

### Ficha 2: Persona secundaria — “El dueño organizado”

- **Nombre:** Roberto Salinas, 41 años, Contador (trabaja desde casa)
- **Mascotas:** 2 gatos — Mishu (7, condición crónica) y Coco (2)
- **Perfil:** meticuloso, hoy lleva libreta física pero no puede buscar información antigua ni comparar evolución de peso. Quiere detalle: notas del veterinario, medicamentos exactos, observaciones.
- **Necesidades:** historial completo y detallado por mascota, búsqueda precisa de eventos pasados, manejo independiente de múltiples mascotas con condiciones distintas.
- **Escenario:** antes de llevar a Mishu a control revisa peso, última medicación y notas anteriores; al volver registra peso actualizado, nueva indicación y próxima fecha, sin mezclar con Coco.

---

## ✅ Requisitos funcionales

### Registro y gestión de mascotas
1. El usuario podrá registrar una nueva mascota indicando nombre, especie, raza, fecha de nacimiento, foto y peso.
2. El usuario podrá editar o eliminar los datos de una mascota ya registrada.
3. El sistema calculará y mostrará automáticamente la edad de la mascota a partir de su fecha de nacimiento.
4. El usuario podrá gestionar más de una mascota dentro de la misma app.

### Registro de eventos de salud
5. El usuario podrá registrar un nuevo evento de salud (vacuna, desparasitación, control veterinario, medicación, baño) asociado a una mascota, indicando fecha, tipo de evento, notas y próxima fecha de recordatorio.
6. El usuario podrá editar o eliminar un evento de salud ya registrado.
7. El sistema permitirá registrar un evento en menos de un minuto, con la menor cantidad de pasos posible.

### Consulta e historial
8. El usuario podrá visualizar el historial completo de eventos de una mascota ordenado cronológicamente, tipo línea de tiempo.
9. El usuario podrá filtrar o distinguir visualmente los eventos por tipo.
10. El usuario podrá consultar una ficha resumen de la mascota tipo “carné” con su información básica y últimos eventos.

### Recordatorios
11. El sistema enviará una notificación local al usuario cuando se acerque la fecha registrada para el próximo evento de salud.

### Configuración
12. El usuario podrá configurar preferencias (unidad de peso kg/lb, activar/desactivar notificaciones, tema claro/oscuro), persistidas localmente.

### General
13. El sistema mostrará una pantalla de bienvenida (Splash Screen) al iniciar la aplicación.
14. Toda la información se almacenará y estará disponible sin necesidad de conexión a internet.

---

## 🛠️ Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Plataforma:** Android nativo
- **Build:** Gradle con Kotlin DSL + Version Catalogs (`libs.versions.toml`)
- **Java:** 11
- **minSdk:** 24 (Android 7.0) · **targetSdk / compileSdk:** 37
- **Tests:** JUnit, Espresso, Compose UI Test

> Estado actual: el repositorio contiene la plantilla base de Android + Compose. La persistencia local (Room/DataStore) y notificaciones locales (NotificationManager / WorkManager) están definidas como parte del diseño offline-first y pendientes de implementación según los requisitos.

---

## 📁 Estructura del proyecto

```
Petcard/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/example/petcard/
│       │   │   ├── MainActivity.kt
│       │   │   └── ui/theme/   # Color.kt, Theme.kt, Type.kt
│       │   ├── res/
│       │   └── AndroidManifest.xml
│       ├── test/               # Unit tests
│       └── androidTest/        # Instrumented tests
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

Evolución prevista (offline-first):

- `data/local/` → Room (Mascota, EventoSalud), DataStore (preferencias)
- `domain/` → casos de uso (registrar mascota, registrar evento, calcular edad)
- `ui/` → navegación Compose: Splash, Lista Mascotas, Ficha Carné, Timeline, Form Evento, Configuración
- `notifications/` → recordatorios locales programados

---

## 🚀 Instalación y ejecución

### Requisitos

- Android Studio Ladybug o superior
- JDK 11+
- Android SDK 37 + emulador o dispositivo físico (Android 7.0+)

### Pasos

```bash
# 1. Clonar
git clone <url-del-repo>
cd Petcard

# 2. Abrir en Android Studio
# File > Open > carpeta Petcard > dejar que Gradle sincronice

# 3. Ejecutar
# Run > Run 'app' (Shift+F10) en emulador o dispositivo
```

Por línea de comandos:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

---

## 📲 Uso previsto

1. Abrir la app (Splash Screen).
2. Registrar una mascota: nombre, especie, raza, nacimiento, foto, peso → la edad se calcula sola.
3. Registrar evento de salud: elegir mascota → tipo (vacuna, desparasitación, control, medicación, baño) → fecha + notas + próxima fecha.
4. Consultar ficha “carné” y timeline cronológico filtrado por tipo.
5. Recibir notificación local antes de la próxima fecha.
6. Ajustar en Configuración: kg/lb, notificaciones, tema claro/oscuro.

---

## 🗺️ Roadmap

- [x] Definición general, benchmarking, personas y requisitos (Doc. 06-sep-2026)
- [x] Plantilla base Android + Compose
- [ ] Modelo de datos local + CRUD mascotas
- [ ] CRUD eventos + timeline + filtros
- [ ] Ficha resumen tipo carné + cálculo de edad
- [ ] Notificaciones locales de recordatorios
- [ ] Configuración (kg/lb, tema, notificaciones)
- [ ] Prototipos de baja/alta fidelidad (pendiente adjuntar capturas)
- [ ] Tests y pulido UX (<1 min por registro)

---

## 📄 Documento base

Este README se elaboró a partir de `Definición general del proyecto final de asignatura de petcard.docx` (prototipos de bajo/alto nivel aún sin imágenes adjuntas en el documento).

---

*PetCard — tu carné digital, siempre en el bolsillo, incluso sin internet.*
