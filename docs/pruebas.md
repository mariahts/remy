# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado y qué queda pendiente.
Documento actualizado el 13 de noviembre de 2026. Solo se citan pruebas que
existen en el repositorio.

**Recuento actual:** 54 pruebas unitarias en 10 suites (todas en verde) y 22
pruebas instrumentadas en 7 suites.

## Entorno

- Emulador de Android Studio: `Medium_Phone_API_37.0` (API 37), dispositivo
  `emulator-5554` (cuando aplica).
- Comandos, desde la raíz del proyecto y con el JBR de Android Studio como
  `JAVA_HOME`:
  - `gradlew testDebugUnitTest` — pruebas unitarias.
  - `gradlew connectedDebugAndroidTest` — pruebas instrumentadas en el emulador.
  - `gradlew assembleDebug` — APK de depuración.
- En esta sesión se ejecutaron `testDebugUnitTest` y `assembleDebug` tras cada
  tarea implementada, además de `assembleRelease` para la versión de entrega
  (resultado: BUILD SUCCESSFUL). Las 22 pruebas instrumentadas se ejecutaron en
  el emulador y quedaron en verde.

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
| `FechaEspanolTest` | Formato de fecha en español para «Cocinada el …» y nombre del mes en minúsculas. |
| `ColorRecetaTest` | Reparto estable del color pastel de cada receta: mismo nombre, mismo tono, rango válido y variedad de tonos. |
| `DatosDemoTest` | Despensa de demostración: unidades en gramos o mililitros, cantidades mayores que cero y al menos una receta completa con lo sembrado. |
| `MacrosRecetaTest` | Cálculo puro de macros de una receta a partir de sus ingredientes: suma proporcional, el aceite solo aporta grasas, receta vacía, ingredientes ausentes del catálogo y redondeo. |

### Instrumentadas (`app/src/androidTest`, emulador)

| Suite | Qué comprueba |
|---|---|
| `AlimentoDaoTest` | Alta/lectura en la despensa y ocultar un alimento sin borrar la fila. |
| `RecetaDaoTest` | Receta con ingredientes en transacción, edición/borrado y observable de la lista. |
| `PrecargaRecetasTest` | Precarga inicial, ausencia de duplicados al reabrir y datos básicos de las recetas. |
| `Migration7To8Test` | Migración de Room de la versión 7 a la 8: se conservan los datos del usuario y las recetas quedan con hidratos y grasas. |
| `EstadoCargaUiTest` | Estados de carga, error con reintento y contenido disponible. |
| `FiltrosSugerenciasUiTest` | Pantalla/sheet de filtros: controles, cambios, limpiar filtros y visibilidad de acciones. |
| `CatalogoRecetasTest` | Catálogo de recetas: los macros de cada receta coinciden con los calculados a partir de sus ingredientes, todos los alimentos declaran hidratos y grasas, y todas las recetas tienen al menos tres pasos y dos ingredientes válidos. |

## Pruebas manuales y verificación

- Compilación y pruebas unitarias verificadas en sesión con
  `assembleDebug` y `testDebugUnitTest` (54 en verde).
- Pruebas instrumentadas ejecutadas en el emulador `Medium_Phone_API_37.0`
  (Android 17) con `connectedDebugAndroidTest` (22 en verde), incluidas las de
  `CatalogoRecetasTest`, que validan la coherencia del catálogo.
- Comprobaciones manuales previas en emulador (recorrido, precarga, CRUD de
  despensa, recetas/sugerencias, filtros, cocinados, descuento y migraciones)
  se documentaron en iteraciones anteriores.
- **Prueba en un dispositivo físico**: la aplicación se ha instalado y probado
  en un móvil real y funciona correctamente. Esa prueba destapó los dos
  últimos defectos de la lista siguiente (el registro y el calendario de
  Inicio), que se han corregido y verificado después en el emulador.

## Defectos encontrados en pruebas manuales y corregidos

Estas comprobaciones se hicieron a partir del feedback de uso en un móvil real,
reproduciendo el flujo en el emulador `Medium_Phone_API_37.0` (Android 17).

| Defecto | Causa raíz | Corrección |
| --- | --- | --- |
| El botón «Añadir» de la hoja del plan se veía cortado como «Añadi». | `OutlineButton` tenía `fillMaxWidth()` forzado y 24 dp de padding horizontal; con el ancho fijo de 92 dp solo quedaban 44 dp para el texto. | `OutlineButton` acepta ahora `contentPadding` y `fillWidth`; la hoja usa `widthIn(min = 88.dp)`, 16 dp de padding y sin relleno. |
| El título decía «Añadir a el desayuno». | Las cadenas `plan_desayuno`, `plan_comida` y `plan_cena` ya traían el artículo («el desayuno»…) y la plantilla era «Añadir a %1$s». | Plantilla «Añadir %1$s» y cadenas «al desayuno», «a la comida», «a la cena». |
| El enlace «Crear cuenta» no hacía nada con los campos vacíos. | El `Text` solo era pulsable con `formularioValido`, así que no llegaba ni a ejecutarse la validación. | Ahora es pulsable siempre y muestra el error de validación correspondiente. |
| Al registrarse, la app parecía no hacer nada. | El proyecto de Supabase exige confirmar el correo (`mailer_autoconfirm = false`), así que `signUpWith` no devuelve sesión y la app se queda en el login. | Se explica el paso de confirmación, se ofrece reenviar el correo y, si la cuenta ya existía, se entra directamente. |
| El reenvío del correo de confirmación a veces fallaba. | Límite de Supabase: `HTTP 429 over_email_send_rate_limit` («solo puedes pedirlo tras 35 segundos»). No es un fallo de la app. | Mensaje explicativo en lugar de un error genérico. |
| Al tocar otro día del calendario de Inicio, la pantalla parpadeaba y perdía la posición del scroll. | `PlanViewModel.estado` emitía el estado «Cargando» dentro del `flatMapLatest`, así que se re-emitía en cada cambio de día y `ContenidoConEstado` sustituía todo el contenido por el indicador de carga. | Se emite «Cargando» solo en la primera carga; mientras llegan los datos del día nuevo se mantiene el día anterior en pantalla. El día resaltado se mueve de inmediato. |
| Los macros de las recetas no coincidían con los calculados a partir de sus ingredientes. | Los hidratos y las grasas se leían del JSON con `getInt`, que trunca los decimales, y `MacrosAlimento` los guardaba como `Int`, así que se perdía la parte decimal. Además el redondeo de Python no coincide con el `roundToInt()` de Kotlin en los empates. | Macros en `Double` leídos con `optDouble`, redondeo `roundToInt()` (half up) y recálculo del catálogo con el mismo criterio. |

Verificación tras la corrección, en el emulador: registro con una cuenta ya
existente (entra directamente en la app), registro con una cuenta nueva (aviso de
confirmación y enlace de reenvío), login normal, y hoja del plan con el título
«Añadir al desayuno» y el botón «Añadir» completo.

## Limitaciones y pendientes

- La prueba manual de «cocinar con foto y reiniciar» queda recomendada en
  dispositivo o emulador actual; el flujo tiene cobertura parcial por pruebas
  automatizadas existentes.
- La app ya se ha probado en un móvil físico y funciona. No se ha comprobado
  con la fuente de letra ampliada ni en un equipo de gama baja.
- Los macros de las recetas se calculan a partir de los valores nutricionales
  estándar por 100 g/ml de cada ingrediente del catálogo, por lo que son
  coherentes entre sí. Antes de publicar conviene contrastarlos con una fuente
  de referencia (por ejemplo, la Base de Datos Española de Composición de
  Alimentos).
- Falta la prueba con 2-3 personas ajenas al equipo.