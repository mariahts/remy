# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado, qué defectos aparecieron
y qué queda pendiente. Versión del 8 de noviembre de 2026 sobre la rama `dev`
(commit `c73a843`).

## Entorno

- Emulador de Android Studio: `Medium_Phone_API_37.0` (API 37), dispositivo
  `emulator-5554`.
- Comandos, desde la raíz del proyecto y con el JBR de Android Studio como
  `JAVA_HOME`:
  - `gradlew testDebugUnitTest` — pruebas unitarias.
  - `gradlew connectedDebugAndroidTest` — pruebas instrumentadas en el emulador.
  - `gradlew assembleDebug` — APK de depuración.
- Todas las pruebas de esta versión se ejecutaron el 8 de noviembre de 2026
  sobre el commit `c73a843` de `dev` (compilación completa: 0 fallos).

## Pruebas automatizadas

### Unitarias — 26 tests, 0 fallos

| Suite | Tests | Qué comprueba |
|---|---|---|
| `ValidacionAlimentoTest` | 6 | Validación del formulario de despensa: nombre vacío, cantidad cero o no numérica, unidad vacía; acepta coma decimal. |
| `AlgoritmoSugerenciasTest` | 4 | Clasificación en completas y casi cocinables por identificador de alimento, máximo configurable de faltantes y alimentos repetidos contados una sola vez. |
| `PreferenciasRepositoryTest` | 3 | Valores por defecto de DataStore, guardar y recuperar filtros y m?ximo, y reapertura de la app (persistencia). |
| `AlmacenamientoFotosTest` | 3 | Nombres de archivo de la foto del plato (`receta_<id>_<fecha>.jpg`) y copia de la imagen al almacenamiento interno (issue #35). |
| `FechaEspanolTest` | 3 | Formato de fecha en español para la tarjeta «Cocinada el …» (issue #35). |

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
| Tema de la aplicación (#8) | Abrir las cuatro pestañas con el tema nuevo | Funciona: paleta, tipografía y formas de `docs/Identidad visual de REMY.md` aplicadas en todas las pantallas desde `REMYTheme`. |
| Alta de alimento con error | Escribir cantidad `0` o dejar la unidad vacía y guardar | Funciona: aparece el mensaje de validación y no se guarda. |
| Listado de recetas | Pestaña Recetas | Funciona: 20 tarjetas con nombre, tiempo y tipo de plato. |
| Detalle de receta | Tocar una tarjeta y volver con «Volver al listado» | Funciona: ingredientes, pasos, nutrición y región. Receta inexistente: «No se encontró esta receta», sin cierre de la app. |
| Sugerencias con la despensa llena | Pestaña Sugerencias | Funciona: «Se puede cocinar ya (20)» y «Casi cocinable (0)». |
| Sugerencias con un ingrediente fuera | Borrar «Arroz» de la despensa | Funciona: «Casi cocinable (3)» con «Falta: Arroz» en las recetas que lo llevan. |
| Preferencias persistentes | Elegir filtro «Postre», cerrar y reabrir la app | Funciona: se recuperan los valores guardados (DataStore). |
| Imagen del selector (#26) | Pulsar «Elegir imagen de la galería», elegir una y cerrar/reabrir la app | Persistencia comprobada con el test `PreferenciasRepositoryTest`; la muestra con Coil y marcador de reserva si no carga. |
| Migraciones de base de datos | Instalar la APK anterior sobre la nueva (v1→v2, v2→v3, v3→v4 y v4→v5) | Funciona: la app arranca sin cierres y conserva los datos. |
| Receta marcada como cocinada (#35) | Marcar una receta como cocinada con foto, reiniciar y comprobar el registro | Pendiente de prueba manual en dispositivo; el flujo está cubierto por los tests unitarios de nombres, fechas y copia. |

## Defectos detectados y corregidos

1. **Convertidor de pasos no reconocido por Room** (`pasos: List<String>`):
   registrado en un `object` con `@TypeConverters` en la entidad `Receta`.
2. **Relación de detalle sin entidad hijo**: se indicó
   `@Relation(entity = Ingrediente::class, ...)` en `RecetaDetalle`.
3. **Test de DataStore**: import en el paquete `core` y cierre del alcance con
   `coroutineContext.job.join()` antes de reabrir el fichero.
4. **Cierre de la app al lanzar `dev` con datos de una versión superior**
   (migración inversa 4→3): se resolvió fusionando la issue #34 y limpiando
   los datos (`adb shell pm clear com.remy.app`).
5. **La precarga desapareció al aplicar el tema (#8)**: el commit del tema
   rehizo el `onCreate` de `MainActivity` y eliminó la llamada a
   `PrecargaRecetas.siHaceFalta(...)`. Detectada al revisar la PR #65; se
   restauró en la PR #67 y se comprobó con instalación limpia.
6. **Conflicto entre #35 y #26 en la pantalla de c?mara**: ambas tocaban la
   misma pantalla (entonces `CamaraScreen.kt`); se resolvi? en la PR #69
   extrayendo los componentes compartidos (`PermisoCamara`, `CamaraConPermiso`,
   `AlternativaGaleria`, `EstadoPermiso`) a `ui/components/CapturaFoto.kt`. La
   pantalla de c?mara independiente se retir? despu?s porque su funcionalidad ya
   estaba integrada en el flujo de cocci?n.

## Limitaciones y pendientes

- Los filtros guardados con DataStore son por tipo de plato; tiempo,
  calorías, macronutrientes y región quedan para la issue #37.
- La prueba manual de «cocinar con foto y reiniciar» (#35) queda para
  dispositivo real; el flujo está cubierto por tests unitarios.
- No se ha probado en un dispositivo físico (issue #48): todo se verificó en
  el emulador.
