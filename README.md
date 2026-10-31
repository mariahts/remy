# REMY

Aplicación móvil Android que sugiere recetas a partir de los ingredientes
que tienes en casa.

## Integrantes
- María José Sánchez León (GitHub: [mariahts](https://github.com/mariahts))
- Mario Jiménez Ruiz (GitHub: [FedericoCamposMartinez](https://github.com/FedericoCamposMartinez))

Proyecto integrador de PMDM · 2.º DAM · curso 2026–2027.

## Problema que resuelve
Muchas veces no sabemos qué cocinar con lo que tenemos en casa, y acabamos
comprando de más y tirando comida. REMY propone recetas según tu despensa y
te dice qué ingredientes te faltan.

## Personas usuarias
Estudiantes, personas que viven solas o en piso compartido y familias que
cocinan en casa con poco tiempo y presupuesto ajustado.

## Qué la hace diferente
- Parte de la despensa real de la persona usuaria, no de una búsqueda por plato.
- Distingue recetas que ya se pueden cocinar y recetas "casi cocinables"
  (con un máximo configurable de ingredientes que faltan).
- Al cocinar una receta descuenta los ingredientes y guarda una foto del plato.

## Funcionalidades de la primera versión
- Despensa personal: alimentos, cantidades y unidades (Room).
- Catálogo de al menos 20 recetas con tiempo e información nutricional.
- Sugerencias según la despensa: completas y con pocos ingredientes faltantes.
- Filtros por tiempo, calorías y macronutrientes, tipo de plato y región,
  con preferencias guardadas (DataStore).
- Marcar una receta como cocinada, con fecha y fotografía del plato
  (función multimedia).

## Si hay tiempo
Registro e inicio de sesión, lista de la compra con cálculo de gasto, planes de
comidas y calendario, valoraciones, recetas propias con foto, modo sin
conexión, sugerencias por patrones de consumo y funciones sociales.

## Tecnologías
- Kotlin y Jetpack Compose (Material 3)
- Arquitectura MVVM (ViewModel y repositorios)
- Navigation Compose (ya en uso: barra inferior con tres pantallas)
- Room para los datos estructurados (ya en uso: la despensa, con el esquema
  exportado a `app/schemas/` para poder migrar versiones)
- CameraX para la función multimedia (prototipo en marcha: permiso, vista
  previa y captura de fotografía)
- DataStore para las preferencias y Coil para mostrar imágenes (previstos)

## Estructura del proyecto
El código está en `app/src/main/java/com/remy/app/`:

- `data`: acceso a datos (entidad `Alimento`, DAO, `RemyDatabase`,
  repositorios; más adelante DataStore).
- `domain/model`: clases de negocio, como Receta o Alimento.
- `ui`: interfaz con Jetpack Compose.
  - `screens`: pantallas (despensa, recetas, sugerencias y prototipo de cámara).
  - `components`: elementos reutilizables.
  - `navigation`: rutas y navegación entre pantallas.
  - `viewmodel`: ViewModels de cada pantalla.
  - `theme`: colores, tipografía y tema.

## Cómo compilar y ejecutar
Requisitos: Android Studio (versión estable reciente) y un emulador Android o
un móvil con la depuración USB activada. La app usa minSdk 26.

1. Clonar el repositorio: `git clone https://github.com/mariahts/remy.git`
2. En Android Studio: **File → Open** y elegir la carpeta `remy`.
3. Esperar a que termine la sincronización de Gradle.
4. Crear o elegir un dispositivo en **Device Manager**.
5. Pulsar **Run** sobre el módulo `app`.

No se necesita ninguna clave ni servicio externo para ejecutar la aplicación.
La app pide permiso de cámara al abrir la pantalla de cámara; se puede denegar
y sigue funcionando con el selector de fotos del sistema.

## Organización del equipo
- Tablero Kanban (GitHub Project): https://github.com/users/mariahts/projects/1
- Columnas: Backlog, Ready, En progreso, En revision, Hecho y Blocked.

### Flujo de trabajo
1. Cada tarea tiene una issue asignada a una persona.
2. Se crea una rama desde `dev` (ejemplo: `feature/22-room-despensa`).
3. Se hacen commits descriptivos y se abre una Pull Request con `Closes #N`.
4. La otra persona revisa la PR, deja una observación concreta y la aprueba.
5. Se hace merge a `dev` y la issue pasa a Hecho.

No se hacen commits directos a `main`: está protegida y exige una aprobación.
El avance se integra en `dev` y `main` se actualiza al cerrar cada hito.

## Estado actual
- Repositorio, tablero con sus columnas y campos, protección de `main` y
  plantillas de issue y de PR listos; las 40 issues están clasificadas con
  etiquetas de RA, tamaño, prioridad y sprint.
- Proyecto Android con Kotlin, Compose y Material 3 que compila y se ejecuta
  en el emulador.
- Navegación con barra inferior y tres pantallas (#7).
- Diagrama del modelo de datos en `docs/ModeloE_R_Remy.drawio` (#11).
- Despensa con Room: entidad, DAO, base de datos y repositorio, con esquema
  exportado y test del DAO (#22).
- Prototipo de cámara con CameraX: permiso, vista previa y captura (#25).
- Pendientes del hito: tema (#8), bocetos (#9 y #10) y la pantalla de despensa
  (#24, bloqueada hasta que esté el tema).

## Documentación
La memoria del proyecto (Word) se entrega en el Aula Virtual. En la carpeta
`docs/` están el modelo de datos (`.drawio`) y el registro de uso de IA
(`uso-ia.md`); se irán añadiendo los bocetos y las pruebas (`pruebas.md`).
