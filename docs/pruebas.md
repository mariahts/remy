# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado, qué defectos aparecieron
y qué queda pendiente. Versión del 4/11/2026 sobre la rama `dev`.

## Entorno

- Emulador de Android Studio: `Medium_Phone_API_37.0` (API 37), dispositivo
  `emulator-5554`.
- Comandos, desde la raíz del proyecto y con el JBR de Android Studio como
  `JAVA_HOME`:
  - `gradlew testDebugUnitTest` — pruebas unitarias.
  - `gradlew connectedDebugAndroidTest` — pruebas instrumentadas en el emulador.
  - `gradlew assembleDebug` — APK de depuración.
- Todas las pruebas de esta versión se han ejecutado el 4/11/2026 sobre el
  commit `504d00b` de `dev`.

## Pruebas automatizadas

### Unitarias — 14 tests, 0 fallos

| Suite | Tests | Qué comprueba |
|---|---|---|
| `ExampleUnitTest` | 1 | Ejemplo de la plantilla del proyecto. |
| `ValidacionAlimentoTest` | 6 | Validación del formulario de despensa: nombre vacío, cantidad cero o no numérica, unidad vacía; acepta coma decimal. |
| `AlgoritmoSugerenciasTest` | 4 | Clasificación en completas y casi cocinables por identificador de alimento (no por nombre), máximo configurable de faltantes y alimentos repetidos contados una sola vez. |
| `PreferenciasRepositoryTest` | 3 | Valores por defecto de DataStore, guardar y recuperar filtros y máximo, y reapertura de la app. |

### Instrumentadas (emulador) — 9 tests, 0 fallos

| Suite | Tests | Qué comprueba |
|---|---|---|
| `AlimentoDaoTest` | 2 | Alta y lectura en la despensa; ocultar un alimento sin borrar la fila. |
| `RecetaDaoTest` | 3 | Receta con ingredientes en una transacción, edición, borrado y observable de la lista. |
| `PrecargaRecetasTest` | 3 | Las 20 recetas se cargan en el primer arranque, no se duplican al reabrir y todas traen tiempo, pasos, tipo de plato y región. |
| `ExampleInstrumentedTest` | 1 | Ejemplo de la plantilla del proyecto. |

## Pruebas manuales en el emulador

| Caso | Pasos | Resultado |
|---|---|---|
| Recorrido principal | Instalar la APK y recorrer la barra inferior: Despensa, Recetas, Sugerencias y Cámara | Funciona: las cuatro pantallas se abren y se puede volver atrás. |
| Primer arranque con precarga | Instalar sobre datos limpios (`adb shell pm clear com.remy.app`) y abrir la app | Funciona: entran 20 recetas y 30 alimentos; la despensa muestra la lista. |
| Alta de alimento con error | Escribir cantidad `0` o dejar la unidad vacía y guardar | Funciona: aparece el mensaje de validación y no se guarda. |
| Listado de recetas | Pestaña Recetas | Funciona: 20 tarjetas con nombre, tiempo y tipo de plato; estado vacío si no hay recetas. |
| Detalle de receta | Tocar una tarjeta y volver con el botón «Volver al listado» | Funciona: ingredientes con su cantidad, pasos numerados, nutrición y región. Receta inexistente (id inválido): «No se encontró esta receta», sin cierre de la app. |
| Sugerencias con la despensa llena | Pestaña Sugerencias | Funciona: «Se puede cocinar ya (20)» y «Casi cocinable (0)». |
| Sugerencias con un ingrediente fuera | Borrar «Arroz» de la despensa y volver a Sugerencias | Funciona: aparece «Casi cocinable (3)» con «Falta: Arroz» en las recetas que lo llevan (el nombre se resuelve desde el catálogo). |
| Preferencias persistentes | Seleccionar el filtro «Postre» y cerrar la app; cambiar el máximo con el stepper | Funciona según el test unitario `PreferenciasRepositoryTest`: al reabrir se recuperan los valores guardados. |
| Migraciones de base de datos | Instalar la APK de la versión anterior por encima de la nueva (v1→v2, v2→v3 y v3→v4) | Funciona: la app arranca sin cierres y conserva los datos. |
| Cámara (de la Entrega 2) | Pestaña Cámara: permiso concedido y permiso denegado | Sin cambios en esta entrega: permiso no obligatorio, vista previa, captura y respaldo con el selector de fotos. |

## Defectos detectados y corregidos

1. **Convertidor de pasos no reconocido por Room** (`pasos: List<String>`):
   el primer intento registraba el convertidor solo en `@Database` y KSP
   fallaba al guardar la propiedad. Corregido usando un `object` con
   `@TypeConverters` en la entidad `Receta`.
2. **Relación de detalle sin entidad hijo**: al definir `RecetaDetalle`, KSP no
   encontraba la columna `recetaId` hasta que se indicó
   `@Relation(entity = Ingrediente::class, ...)`.
3. **Test de DataStore**: import de `PreferenceDataStoreFactory` en el paquete
   `core` y cierre del alcance con `coroutineContext.job.join()` antes de
   reabrir el fichero.
4. **Cierre de la app al lanzar `dev` con datos de una versión superior**
   («A migration from 4 to 3 was required»): la base del emulador estaba en la
   versión 4 y el código de `dev` aún era el 3. Se resolvió fusionando la issue
   #34 en `dev` y, para seguir probando, limpiando los datos de la app con
   `adb shell pm clear com.remy.app`.

## Limitaciones y pendientes

- La pantalla de despensa está bloqueada hasta que aplique el tema de la issue
  #8 (#24, «Blocked»); los criterios de tema siguen sin marcarse.
- Los filtros guardados con DataStore son por tipo de plato; tiempo,
  calorías, macronutrientes y región quedan para la issue #37.
- La cámara es un prototipo (#25): aún no se guarda la foto al marcar una
  receta como cocinada.
- No se han hecho pruebas en un dispositivo físico (issue #48): por ahora todo
  se ha verificado en el emulador.
- `main` todavía no recibe el código de esta entrega: la integración de
  `dev` en `main` la realizará Mario.
