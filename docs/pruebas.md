# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado, qué defectos aparecieron
y qué queda pendiente. Versión del 10 de noviembre de 2026 sobre la rama `dev`
(commit `7b8bead`), el commit local `da75ae3` de las issues #36, #43, #44 y #45,
y los cambios locales de las issues #37, #46 y #38.

## Entorno

- Emulador de Android Studio: `Medium_Phone_API_37.0` (API 37), dispositivo
  `emulator-5554`.
- Comandos, desde la raíz del proyecto y con el JBR de Android Studio como
  `JAVA_HOME`:
  - `gradlew testDebugUnitTest` — pruebas unitarias.
  - `gradlew connectedDebugAndroidTest` — pruebas instrumentadas en el emulador.
  - `gradlew assembleDebug` — APK de depuración.
- Todas las pruebas de esta versión se ejecutaron el 10 de noviembre de 2026
  sobre el commit `7b8bead` de `dev` más los cambios locales indicados arriba
  (compilación completa: 0 fallos).

## Pruebas automatizadas

### Unitarias — 37 tests, 0 fallos

| Suite | Tests | Qué comprueba |
|---|---|---|
| `ValidacionAlimentoTest` | 6 | Validación del formulario de despensa: nombre vacío, cantidad cero o no numérica, unidad vacía; acepta coma decimal. |
| `AlgoritmoSugerenciasTest` | 8 | Clasificación en completas y casi cocinables por identificador de alimento, máximo configurable de faltantes, alimentos repetidos contados una sola vez, despensa vacía, receta sin ingredientes, máximo 0 y receta sin ningún ingrediente disponible. |
| `DescuentoDespensaTest` | 5 | Descuento de la despensa al cocinar (#36): cantidades normales, alimento que no está en la despensa, cantidad que nunca baja de cero, varios ingredientes del mismo alimento y unidades distintas. |
| `FiltroRecetasTest` | 7 | Filtros de sugerencias (#37): sin filtros, tipo de plato, región sin distinguir mayúsculas, tiempo máximo y recetas sin tiempo conocido, calorías y proteínas, combinación de todos y límites a cero. |
| `PreferenciasRepositoryTest` | 5 | Valores por defecto de DataStore, guardado y lectura de los seis filtros (#37), límites del máximo de faltantes, persistencia al reabrir y limpieza de filtros. |
| `AlmacenamientoFotosTest` | 3 | Nombres de archivo de la foto del plato (`receta_<id>_<fecha>.jpg`) y copia de la imagen al almacenamiento interno (issue #35). |
| `FechaEspanolTest` | 3 | Formato de fecha en español para la tarjeta «Cocinada el …» (issues #35 y #44). |

### Instrumentadas (emulador) — 16 tests, 0 fallos

| Suite | Tests | Qué comprueba |
|---|---|---|
| `AlimentoDaoTest` | 2 | Alta y lectura en la despensa; ocultar un alimento sin borrar la fila. |
| `RecetaDaoTest` | 3 | Receta con ingredientes en una transacción, edición, borrado y observable de la lista. |
| `PrecargaRecetasTest` | 3 | Las 20 recetas se cargan en el primer arranque, no se duplican al reabrir y todas traen tiempo, pasos, tipo de plato y región. |
| `EstadoCargaUiTest` | 3 | Estados de carga y error (#46): mensaje de carga, mensaje de error con «Reintentar» y contenido cuando los datos están disponibles. |
| `FiltrosSugerenciasUiTest` | 5 | Pantalla de filtros (#37 y #38): muestra los seis filtros y el contador, avisa al elegir un tipo de plato o un tiempo máximo, permite limpiar los filtros y oculta el botón cuando no hay ninguno activo. |

## Pruebas manuales en el emulador

| Caso | Pasos | Resultado |
|---|---|---|
| Recorrido principal | Instalar la APK y recorrer la barra inferior: Despensa, Recetas, Cocinadas y Sugerencias | Funciona: las cuatro pantallas se abren y se puede volver atrás. |
| Primer arranque con precarga | Instalar sobre datos limpios (`adb shell pm clear com.remy.app`) y abrir la app | Funciona: entran 20 recetas y 30 alimentos; la despensa muestra la lista. |
| Tema de la aplicación (#8) | Abrir las cuatro pestañas con el tema nuevo | Funciona: paleta, tipografía y formas de `docs/Identidad visual de REMY.md` aplicadas en todas las pantallas desde `REMYTheme`. |
| Alta de alimento con error | Escribir cantidad `0` o dejar la unidad vacía y guardar | Funciona: aparece el mensaje de validación y no se guarda. |
| Listado de recetas | Pestaña Recetas | Funciona: 20 tarjetas con nombre, tiempo y tipo de plato. |
| Detalle de receta | Tocar una tarjeta y volver con «Volver al listado» | Funciona: ingredientes, pasos, nutrición y región. Receta inexistente: «No se encontró esta receta», sin cierre de la app. |
| Sugerencias con la despensa llena | Pestaña Sugerencias | Funciona: «Se puede cocinar ya (20)» y «Casi cocinable (0)». |
| Sugerencias con un ingrediente fuera | Borrar «Arroz» de la despensa | Funciona: «Casi cocinable (3)» con «Falta: Arroz» en las recetas que lo llevan. |
| Filtros completos (#37) | En Sugerencias, elegir tipo de plato, tiempo máximo, calorías máximas, proteínas mínimas y región | Funciona: la lista y el contador cambian; las etiquetas de los resultados resumen los filtros activos. |
| Persistencia de filtros (#37) | Aplicar varios filtros, cerrar y reabrir la app | Funciona: se recuperan los seis valores guardados en DataStore. |
| Limpiar filtros (#37) | Pulsar «Limpiar filtros» | Funciona: vuelven los valores por defecto y desaparece el botón de limpiar. |
| Historial de cocinadas vacío (#44) | Abrir la pestaña Cocinadas sin haber cocinado nada | Funciona: estado vacío con «Aún no has cocinado ninguna receta» y botón «Ver mis recetas». |
| Marcar como cocinada sin foto (#35 y #44) | Abrir «Arroz con leche», pulsar «Marcar como cocinada» y «Confirmar sin foto» | Funciona: avisa de que el registro queda sin fotografía y la receta aparece en Cocinadas con la fecha del día. |
| Descuento de la despensa (#36) | Cocinar «Arroz con leche» (750 ml de leche y 120 g de azúcar) y revisar la despensa | Funciona: la leche pasa de 1000 a 250 ml y el azúcar de 500 a 380 g; ningún valor queda por debajo de cero. |
| Migraciones de base de datos | Instalar la APK anterior sobre la nueva (v1→v2, v2→v3, v3→v4 y v4→v5) | Funciona: la app arranca sin cierres y conserva los datos. |
| Receta marcada como cocinada con foto (#35) | Marcar una receta con foto desde la cámara o la galería, reiniciar y comprobar el registro | Pendiente de prueba manual en dispositivo; el flujo está cubierto por los tests unitarios de nombres, fechas y copia. |

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
   rehízo el `onCreate` de `MainActivity` y eliminó la llamada a
   `PrecargaRecetas.siHaceFalta(...)`. Detectada al revisar la PR #65; se
   restauró en la PR #67 y se comprobó con instalación limpia.
6. **Conflicto entre #35 y #26 en la pantalla de cámara**: ambas tocaban la
   misma pantalla (entonces `CamaraScreen.kt`); se resolvió en la PR #69
   extrayendo los componentes compartidos (`PermisoCamara`, `CamaraConPermiso`,
   `AlternativaGaleria`, `EstadoPermiso`) a `ui/components/CapturaFoto.kt`. La
   pantalla de cámara independiente se retiró después porque su funcionalidad ya
   estaba integrada en el flujo de cocción.
7. **Pruebas instrumentadas rotas tras el refactor de la interfaz**: al cambiar
   `RecetaConIngredientes` para anidar `IngredienteConAlimento`, los tests
   `RecetaDaoTest` y `PrecargaRecetasTest` seguían leyendo `alimentoId` y
   `cantidad` directamente. Se corrigió el acceso a
   `ingrediente.alimentoId` e `ingrediente.cantidad` y las 8 pruebas volvieron
   a pasar en el emulador.
8. **Importaciones incorrectas en las pruebas de interfaz de filtros**: se
   importaron `assertExists` y `assertDoesNotExist` como funciones sueltas,
   cuando son métodos de la interacción de Compose. Se quitaron las
   importaciones y las 16 pruebas instrumentadas compilaron y pasaron.
9. **Formateo de cantidades duplicado**: `DespensaScreen` y `RecetaScreen`
   tenían cada una su propia función privada `Double.formato()`. Se sustituyó
   por la utilidad compartida `formatoCantidad()` de
   `ui/components/FormatoCantidad.kt`.

## Limitaciones y pendientes

- La prueba manual de «cocinar con foto y reiniciar» (#35) queda para
  dispositivo real; el flujo está cubierto por tests unitarios.
- No se ha probado en un dispositivo físico (issue #48): todo se verificó en
  el emulador. El motivo es que no se dispone de un móvil Android libre para
  instalar la APK; se documenta la limitación en este mismo archivo y se
  mantiene la comprobación en el emulador como evidencia.
- El camino de error de las pantallas (#46) se verificó con estados inyectados
  en `EstadoCargaUiTest` y con el `catch` de los ViewModels; no se ha provocado
  un fallo real de Room en el emulador. Con Room local la carga es tan rápida
  que el indicador apenas se percibe en el uso normal, pero las pantallas ya
  distinguen carga, datos y error con reintento.
