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

### Cómo probar el recorrido principal (Entrega 3)
1. Instalar la APK o pulsar Run: la app abre la pestaña Despensa y, en la
   primera instalación, entran el catálogo de 20 recetas y 30 alimentos.
2. Añadir un alimento con el botón flotante; probar a guardar con cantidad 0
   para ver el mensaje de validación.
3. Pestaña Recetas: tocar una tarjeta para abrir el detalle (ingredientes,
   pasos y nutrición) y volver con «Volver al listado».
4. Pestaña Sugerencias: con la despensa completa aparecen «Se puede cocinar
   ya» y «Casi cocinable»; al borrar un alimento de la despensa (por ejemplo
   «Arroz») aparecen recetas en «Casi cocinable» con «Falta: …».
5. Elegir el filtro «Postre» o cambiar el máximo de faltantes y, con la app
   cerrada, abrirla de nuevo: la preferencia se conserva (DataStore).

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

## Estado actual (Entrega 3, 4 de noviembre de 2026)
- Base de datos Room en la versión 4 con migraciones explícitas: despensa
  (#22 y #28), recetas e ingredientes con su relación (#23) y precarga del
  catálogo de 20 recetas y 30 alimentos en el primer arranque (#30).
- Preferencias con DataStore: filtros de tipo de plato y máximo de
  ingredientes faltantes que se recuperan al reabrir la app (#29).
- Pantallas de listado (#31) y detalle (#32) de recetas, con navegación desde
  la tarjeta y vuelta atrás.
- Sugerencias: algoritmo que separa recetas completas de casi cocinables por
  identificador de alimento (#33) y pantalla que muestra los dos grupos con el
  listado de lo que falta (#34).
- Pruebas: 14 unitarias y 9 instrumentadas en verde, más el recorrido manual;
  el registro está en `docs/pruebas.md`.
- Prototipo de cámara con CameraX: permiso, vista previa y captura (#25).
- Pendientes: tema (#8), bocetos (#9 y #10) y pantalla de despensa (#24,
  bloqueada hasta que aplique el tema); filtros por tiempo, calorías y región
  (#37); receta marcada como cocinada con foto (#35).

## Documentación
La memoria del proyecto (Word) se entrega en el Aula Virtual. En la carpeta
`docs/` están el modelo de datos (`.drawio`), el registro de uso de IA
(`uso-ia.md`) y el registro de pruebas (`pruebas.md`).
