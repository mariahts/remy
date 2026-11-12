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
- Pantalla de inicio con calendario y registro de las recetas cocinadas por día.
- Despensa personal: alimentos, cantidades y unidades (Room).
- Catálogo de al menos 20 recetas con tiempo e información nutricional.
- Sugerencias según la despensa: completas y con pocos ingredientes faltantes.
- Filtros por tipo de plato, tiempo máximo, calorías máximas, proteínas mínimas,
  región y máximo de ingredientes que faltan, con las preferencias guardadas en
  DataStore y un botón para limpiar los filtros (#37).
- Estados de carga y de error con opción de reintentar en las pantallas que
  consultan Room (#46).
- Marcar una receta como cocinada, con fecha y fotografía del plato
  (función multimedia), y registro de lo cocinado en el calendario.
- Al marcar una receta como cocinada se descuentan de la despensa los
  ingredientes que había, sin bajar nunca de cero (#36).

## Si hay tiempo
Registro e inicio de sesión, lista de la compra con cálculo de gasto, planes de
comidas, valoraciones, recetas propias con foto, modo sin conexión, sugerencias
por patrones de consumo y funciones sociales.

## Tecnologías
- Kotlin y Jetpack Compose (Material 3)
- Arquitectura MVVM (ViewModel y repositorios)
- Navigation Compose (barra inferior con tres destinos: Inicio, Despensa y
  Recetas; Sugerencias, Perfil y el detalle de receta se abren desde ellas)
- Room para los datos estructurados (despensa, recetas e historial de
  cocciones, con el esquema exportado a `app/schemas/` para poder migrar)
- CameraX para la foto del plato (permiso, vista previa y captura) y selector
  de fotos del sistema como alternativa
- DataStore para las preferencias de filtros

## Estructura del proyecto
El código está en `app/src/main/java/com/remy/app/`:

- `data`: acceso a datos (entidades, DAO, `RemyDatabase`, repositorios y las
  preferencias de filtros con DataStore).
- `domain/model`: clases de negocio, como Receta o Alimento.
- `ui`: interfaz con Jetpack Compose.
  - `screens`: pantallas (inicio, despensa, recetas, sugerencias, perfil y
    detalle de receta).
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
La app pide permiso de cámara al marcar una receta como cocinada y elegir la
cámara; se puede denegar y seguir funcionando con el selector de fotos del
sistema.

### Cómo probar el recorrido principal
1. Instalar la APK o pulsar Run: la app abre la pantalla de Inicio con el
   calendario y, en la primera instalación, entran el catálogo de 20 recetas y
   30 alimentos.
2. Pestaña Despensa: añadir un alimento con el botón flotante; probar a guardar
   con cantidad 0 para ver el mensaje de validación.
3. Pestaña Recetas: tocar una tarjeta para abrir el detalle (ingredientes,
   pasos y nutrición) y volver con «Volver al listado».
4. En la barra inferior, abrir «Sugerencias»: con la despensa
   completa aparecen «Se puede cocinar ya» y «Casi cocinable»; al borrar un
   alimento de la despensa (por ejemplo «Arroz») aparecen recetas en «Casi
   cocinable» con «Falta: …».
5. En Sugerencias, abrir los filtros, elegir un tipo de plato, un tiempo
   máximo, unas calorías máximas, unas proteínas mínimas, una región o un
   filtro de macros por ración (`Alta en proteína`, `Baja en hidratos`,
   `Baja en grasa`), y comprobar que la lista y el contador cambian. Cerrar y
   abrir la app: los filtros se conservan (DataStore); «Limpiar filtros» vuelve
   al estado inicial.
6. Abrir una receta, pulsar «Empezar a cocinar», bajar hasta «Marcar como
   cocinada» y confirmar con foto o sin ella: la receta queda registrada en el
   calendario de Inicio en el día correspondiente y la despensa descuenta los
   ingredientes (sin bajar nunca de cero). Si algún ingrediente no se puede
   descontar, la app avisa con su nombre y la cocción se guarda igualmente.
7. Abrir el icono de perfil: se puede comprobar la información de la cuenta y
   el acceso a las recetas que ya se han cocinado.

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

## Estado actual (12 de noviembre de 2026)
- Base de datos Room en la versión 8 con migraciones explícitas: despensa
  (#22 y #28), recetas e ingredientes (#23), precarga del catálogo (#30),
  receta cocinada con fecha y foto (#35), e hidratos y grasas de recetas (#82).
- Tema de la app (#8): paleta, tipografía y formas aplicados en todas las
  pantallas, con su documento en `docs/disenio/Identidad visual de REMY.md`.
- Preferencias con DataStore (#29), listado (#31) y detalle (#32) de recetas,
  y sugerencias con los dos grupos (#33 y #34).
- Receta marcada como cocinada con foto (#35), registro de recetas cocinadas
  en el calendario de Inicio (#44) y descuento automático de la despensa al
  cocinar (#36).
- Navegación con cuatro destinos en la barra inferior: Inicio, Despensa,
  Recetas y Sugerencias (#45 y #83).
- Filtros completos de #37: tipo de plato, tiempo máximo, calorías máximas,
  proteínas mínimas, región y máximo de faltantes, persistidos en DataStore y
  con opción de limpiarlos.
- Filtros de macros por ración (#82): `Alta en proteína` (≥ 25 g),
  `Baja en hidratos` (≤ 30 g) y `Baja en grasa` (≤ 15 g), con persistencia
  en DataStore.
- Catálogo de 20 recetas y 31 alimentos: las macros (calorías, proteínas,
  hidratos y grasas) de cada receta se **calculan a partir de sus ingredientes**
  al precargar el catálogo, a partir de los valores nutricionales por 100 g/ml de
  cada alimento. Los pasos de elaboración se han detallado y la proteína del
  yogur natural se ha corregido.
- Fotos reales del catálogo: cada receta muestra una imagen procedente de
  Wikimedia Commons con licencia libre; los créditos (autor, licencia y enlace)
  están en `docs/fotos-recetas.md`. La foto que la persona usuaria hace al
  cocinar sigue teniendo prioridad sobre la del catálogo.
- Macros completas en el detalle (#82): proteínas, hidratos y grasas por
  ración, escaladas con el selector de raciones.
- Rediseño de la interfaz (#83): tema Nunito, componentes reutilizables,
  pantallas rediseñadas, planificación semanal y estados vacíos mejorados.
- Pulido de interfaz para la entrega 4: miniaturas de receta con fotos del
  catálogo (o bloques de color pastel si falta la foto), insignia de
  disponibilidad en «Tu receta de hoy», despensa de demostración sembrada con
  las unidades de las recetas, mes visible en la cabecera de la semana y los
  dos accesos directos del inicio («Añadir a mi despensa» y «Ver sugerencias»)
  con la misma altura. La revisión de diseño, accesibilidad y usabilidad está
  en `docs/revision-diseno-usabilidad.md`.
- Estados de carga y error con reintento en las pantallas que consultan Room
  (#46).
- Versión de entrega: APK de `release` con ofuscación (R8) y firma propia,
  de 3,3 MB.
- Pruebas: 54 unitarias y 22 instrumentadas en verde; el registro está en
  `docs/pruebas.md`.
- Pendientes: documentos de la entrega (#41 y #42), verificación en móviles
  reales, caducidad en la despensa, modo oscuro y publicación (icono, capturas,
  política de privacidad).

## Documentación
La memoria del proyecto (Word) se entrega en el Aula Virtual. En la carpeta
`docs/` están el modelo de datos (`.drawio`), el registro de uso de IA
(`uso-ia.md`) y el registro de pruebas (`pruebas.md`).
