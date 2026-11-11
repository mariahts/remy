# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado y qué queda pendiente.
Documento actualizado el 11 de noviembre de 2026 tras el sprint 4 (rama `dev`,
integrada en `main` mediante la PR #85). Solo se citan pruebas que existen en
el repositorio.

**Recuento actual:** 40 pruebas unitarias en 7 suites (todas en verde) y 17
pruebas instrumentadas en 6 suites.

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
| `Migration7To8Test` | Migración de Room de la versión 7 a la 8: se conservan los datos del usuario y las recetas quedan con hidratos y grasas. |
| `EstadoCargaUiTest` | Estados de carga, error con reintento y contenido disponible. |
| `FiltrosSugerenciasUiTest` | Pantalla/sheet de filtros: controles, cambios, limpiar filtros y visibilidad de acciones. |

## Pruebas manuales y verificación

- Compilación y pruebas unitarias verificadas en sesión con
  `assembleDebug` y `testDebugUnitTest`.
- Comprobaciones manuales previas en emulador (recorrido, precarga, CRUD de
  despensa, recetas/sugerencias, filtros, cocinados, descuento y migraciones)
  se documentaron en iteraciones anteriores; repetirlas en dispositivo o
  emulador tras los cambios del sprint 4 es recomendable.

## Limitaciones y pendientes

- La prueba manual de «cocinar con foto y reiniciar» queda recomendada en
  dispositivo/emulador actual; el flujo tiene cobertura parcial por pruebas
  automatizadas existentes.
- No se ha probado en dispositivo físico en esta sesión.
- El sprint 4 incorpora refactors de limpieza (descuento extraído, avisos,
  strings/tema, perfil y ampliación de tests de cruce), el rediseño de la
  interfaz y las macros por ración; validar el flujo de cocción y descuento en
  UI tras estos cambios.
- Las 17 pruebas instrumentadas (incluida `Migration7To8Test`) están escritas
  pero no se han reejecutado en emulador desde el sprint 4: falta lanzar
  `./gradlew :app:connectedDebugAndroidTest` con un emulador abierto.
- Los valores de hidratos y grasas del catálogo de recetas son estimados, no
  oficiales: antes de publicar habría que revisarlos con una fuente fiable.
- Falta la prueba con 2-3 personas ajenas al equipo y la validación en móviles
  reales (letra grande, rotación y gama baja).