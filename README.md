# REMY

Aplicación móvil Android que sugiere recetas a partir de los ingredientes
que tienes en casa.

## Autores
- María José Sánchez León
- Mario Jiménez Ruiz

## Problema que resuelve
Muchas veces no sabemos qué cocinar con lo que tenemos.
La app propone recetas según tu despensa y los ingredientes que te faltan.

## Funcionalidades de la primera versión
- Registro e inicio de sesión
- Despensa personal (alimentos y cantidades)
- Catálogo de recetas
- Sugerencias según la despensa (completas y con pocos ingredientes faltantes)
- Filtros por tiempo, calorías, tipo de plato y región

## Funcionalidades futuras
Lista de la compra, planes de comidas y calendario,
valoraciones, recetas propias con foto, modo offline, sugerencias por
patrones de consumo y funciones sociales.

## Tecnologías
- Kotlin y Jetpack Compose

## Estructura del proyecto

La app sigue la arquitectura MVVM. El código está en
`app/src/main/java/com/remy/app/`:

- `data`: acceso a datos (Room y repositorios). Se rellenará cuando
  se implemente la persistencia.
- `domain/model`: clases de negocio, como Receta o Alimento.
- `ui`: interfaz con Jetpack Compose.
  - `screens`: pantallas.
  - `components`: elementos reutilizables.
  - `navigation`: rutas y navegación entre pantallas.
  - `viewmodel`: ViewModels de cada pantalla.
  - `theme`: colores, tipografía y tema.

## Organización del equipo
- Tablero Kanban: https://github.com/users/mariahts/projects/1

### Flujo de trabajo
1. Cada tarea tiene una issue asignada a una persona.
2. Se crea una rama desde `main` (ejemplo: `feature/7-navegacion`).
3. Se hacen commits y se abre una Pull Request con `Closes #N`.
4. La otra persona revisa y aprueba la PR.
5. Se hace merge a `main` y la issue se cierra.

No se hacen commits directos a `main`.

## Estado actual
Proyecto Android base creado y estructura de paquetes definida.
Aún sin funcionalidades implementadas.