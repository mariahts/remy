# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado y qué queda pendiente.
Documento actualizado el 11 de noviembre de 2026 para la rama
`feature/limpieza-mvp` (trabajo de limpieza/tests sobre MVP). Solo se citan
pruebas que existen en el repositorio.

## Entorno

- Emulador de Android Studio: `Medium_Phone_API_37.0` (API 37), dispositivo
  `emulator-5554` (cuando aplica).
- Comandos, desde la raíz del proyecto y con el JBR de Android Studio como
  `JAVA_HOME`:
  - `gradlew testDebugUnitTest` — pruebas unitarias.
  - `gradlew connectedDebugAndroidTest` — pruebas instrumentadas en el emulador.
  - `gradlew assembleDebug` — APK de depuración.
- En esta sesión se ejecutaron `testDebugUnitTest` y `assembleDebug` tras cada
  tarea de limpieza implementada (resultado: BUILD SUCCESSFUL). Las pruebas
  instrumentadas no se reejecutaron completas en esta sesión salvo donde se
  indique.

## Pruebas automatizadas (existentes)

### Unitarias (`app/src/test`)

| Suite | Qué comprueba |
|---|---|
| `ValidacionAlimentoTest` | Validación del formulario de despensa: nombre vacío, cantidad cero o no numérica, unidad vacía; acepta coma decimal. |
| `AlgoritmoSugerenciasTest` | Clasificación completas/casi cocinables por identificador, máximo configurable de faltantes, ingredientes repetidos, cantidad insuficiente, conversión g/kg y ml/l, y exclusión de básicos (sal, agua, pimienta). |
| `DescuentoDespensaTest` | Descuento puro de despensa al cocinar: descuento normal, cantidad insuficiente (queda en 0), mismo alimento en varias líneas, unidades incompatibles (se omite y se informa) y escalado por raciones. |
| `FiltroRecetasTest` | Filtros de sugerencias: sin filtros, tipo de plato, región, tiempo máximo, calorías, proteínas, combinaciones y límites a cero. |
| `PreferenciasRepositoryTest` | Valores por defecto, guardado/lectura de filtros, límites, persistencia al reabrir y limpieza de filtros en DataStore. |
| `AlmacenamientoFotosTest` | Nombres de archivo de la foto del plato y copia de la imagen al almacenamiento interno. |
| `FechaEspanolTest` | Formato de fecha en español para «Cocinada el …». |

### Instrumentadas (`app/src/androidTest`, emulador)

| Suite | Qué comprueba |
|---|---|
| `AlimentoDaoTest` | Alta/lectura en la despensa y ocultar un alimento sin borrar la fila. |
| `RecetaDaoTest` | Receta con ingredientes en transacción, edición/borrado y observable de la lista. |
| `PrecargaRecetasTest` | Precarga inicial, ausencia de duplicados al reabrir y datos básicos de las recetas. |
| `EstadoCargaUiTest` | Estados de carga, error con reintento y contenido disponible. |
| `FiltrosSugerenciasUiTest` | Pantalla/sheet de filtros: controles, cambios, limpiar filtros y visibilidad de acciones. |

## Pruebas manuales y verificación

- Compilación y pruebas unitarias de la rama verificados en sesión con
  `assembleDebug` y `testDebugUnitTest`.
- Comprobaciones manuales previas en emulador (recorrido, precarga, CRUD de
  despensa, recetas/sugerencias, filtros, cocinadas, descuento y migraciones)
  se documentaron en iteraciones anteriores; repetir en dispositivo/emulador tras
  cambios de esta rama es recomendable.

## Limitaciones y pendientes

- La prueba manual de «cocinar con foto y reiniciar» queda recomendada en
  dispositivo/emulador actual; el flujo tiene cobertura parcial por pruebas
  automatizadas existentes.
- No se ha probado en dispositivo físico en esta sesión.
- La rama actual incorpora refactors de limpieza (descuento extraído, avisos,
  strings/tema, perfil y ampliación de tests de cruce); validar el flujo de
  cocción y descuento en UI tras estos cambios.