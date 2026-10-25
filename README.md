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
- Navigation Compose
- Room y DataStore (previstos)
- CameraX y Coil para la función multimedia (previstos)

## Estructura del proyecto
El código está en `app/src/main/java/com/remy/app/`:

- `data`: acceso a datos (Room, DataStore y repositorios).
- `domain/model`: clases de negocio, como Receta o Alimento.
- `ui`: interfaz con Jetpack Compose.
  - `screens`: pantallas.
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

## Organización del equipo
- Tablero Kanban (GitHub Project): https://github.com/users/mariahts/projects/1
- Columnas: Backlog, Ready, En progreso, En revision, Hecho y Blocked.

### Flujo de trabajo
1. Cada tarea tiene una issue asignada a una persona.
2. Se crea una rama desde `main` (ejemplo: `feature/7-navegacion`).
3. Se hacen commits descriptivos y se abre una Pull Request con `Closes #N`.
4. La otra persona revisa la PR, deja una observación concreta y la aprueba.
5. Se hace merge a `main` y la issue pasa a Hecho.

No se hacen commits directos a `main`: está protegida y exige una aprobación.

## Estado actual
- Repositorio, tablero, protección de `main` y plantillas de issue y PR listos.
- Proyecto Android base (Kotlin, Compose, Material 3) que compila y se ejecuta
  en el emulador con la plantilla inicial.
- Estructura de paquetes MVVM creada.
- Aún no hay funcionalidades de la aplicación implementadas.

## Documentación
La memoria del proyecto (Word) se entrega en el Aula Virtual. En la carpeta
`docs/` está el registro de uso de IA (`uso-ia.md`); se irán añadiendo el
modelo de datos, los bocetos y las pruebas (`pruebas.md`).
