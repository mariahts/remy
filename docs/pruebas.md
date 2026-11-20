# Pruebas de REMY

Qué se ha probado, en qué entorno, con qué resultado y qué queda pendiente.
Documento actualizado el 20 de noviembre de 2026. Solo se citan pruebas que
existen en el repositorio.

**Recuento actual:** 54 pruebas unitarias en 7 suites (todas en verde) y 23
pruebas instrumentadas en 7 suites (todas en verde).

El 20 de noviembre se revisaron los tests y se quitó lo que no aportaba:
las suites de color de receta, de fecha en español y de datos de demo, y los
tres tests de macros por ración (quedaron en uno). Lo que queda cubre la
lógica de la app y los defectos corregidos.

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
  (resultado: BUILD SUCCESSFUL). Las 23 pruebas instrumentadas se ejecutaron en
  el emulador y quedaron en verde.

## Pruebas automatizadas (existentes)

### Unitarias (`app/src/test`)

| Suite | Qué comprueba |
|---|---|
| `ValidacionAlimentoTest` | Validación del formulario de despensa: nombre vacío, cantidad cero o no numérica, unidad vacía; acepta coma decimal; aviso al escribir un ingrediente con mayúsculas o acentos de más y normalización de nombres para no repetir alimentos. |
| `AlgoritmoSugerenciasTest` | Clasificación completas/casi cocinables por identificador, máximo configurable de faltantes, ingredientes repetidos, cantidad insuficiente, conversión g/kg y ml/l, equiparación de g con ml al comprobar cantidades, exclusión de básicos (sal, agua, pimienta) y disponibilidad de cada ingrediente para el badge «Lista» del detalle. |
| `DescuentoDespensaTest` | Descuento puro de despensa al cocinar: descuento normal, cantidad insuficiente (queda en 0), mismo alimento en varias líneas, unidades incompatibles (se omite y se informa) y escalado por raciones. |
| `FiltroRecetasTest` | Filtros de sugerencias: sin filtros, tipo de plato, región, tiempo máximo, calorías, proteínas, macros por ración, combinaciones y límites a cero. |
| `PreferenciasRepositoryTest` | Valores por defecto, guardado/lectura de filtros, límites, persistencia al reabrir y limpieza de filtros en DataStore. |
| `AlmacenamientoFotosTest` | Nombres de archivo de la foto del plato y copia de la imagen al almacenamiento interno. |
| `MacrosRecetaTest` | Cálculo puro de macros de una receta a partir de sus ingredientes: suma proporcional, el aceite solo aporta grasas, receta vacía, ingredientes ausentes del catálogo y redondeo. |

### Instrumentadas (`app/src/androidTest`, emulador)

| Suite | Qué comprueba |
|---|---|
| `AlimentoDaoTest` | Alta/lectura en la despensa y ocultar un alimento sin borrar la fila. |
| `RecetaDaoTest` | Receta con ingredientes en transacción, edición/borrado y observable de la lista. |
| `PrecargaRecetasTest` | Precarga inicial, ausencia de duplicados al reabrir, retirada de las recetas que ya no están en el catálogo y datos básicos de las recetas. |
| `Migration7To8Test` | Migración de Room de la versión 7 a la 8: se conservan los datos del usuario y las recetas quedan con hidratos y grasas. |
| `EstadoCargaUiTest` | Estados de carga, error con reintento y contenido disponible. |
| `FiltrosSugerenciasUiTest` | Pantalla/sheet de filtros: controles, cambios, limpiar filtros y visibilidad de acciones. |
| `CatalogoRecetasTest` | Catálogo de recetas: los macros de cada receta coinciden con los calculados a partir de sus ingredientes, todos los alimentos declaran hidratos y grasas, y todas las recetas tienen al menos tres pasos y dos ingredientes válidos. |

## Pruebas manuales y verificación

- Compilación y pruebas unitarias verificadas en sesión con
  `assembleDebug` y `testDebugUnitTest` (54 en verde).
- Pruebas instrumentadas ejecutadas en el emulador `Medium_Phone_API_37.0`
  (Android 17) con `connectedDebugAndroidTest` (23 en verde), incluidas las de
  `CatalogoRecetasTest`, que validan la coherencia del catálogo.
- Comprobaciones manuales previas en emulador (recorrido, precarga, CRUD de
  despensa, recetas/sugerencias, filtros, cocinados, descuento y migraciones)
  se documentaron en iteraciones anteriores.
- **Prueba en un dispositivo físico**: la aplicación se ha instalado y probado
  en un móvil real y funciona correctamente. Esa prueba destapó los dos
  últimos defectos de la lista siguiente (el registro y el calendario de
  Inicio), que se han corregido y verificado después en el emulador.

## Defectos encontrados en pruebas manuales y corregidos

Estas comprobaciones se hicieron a partir del feedback de uso en un móvil real
y de las cuatro personas de prueba a las que María les pasó la APK,
reproduciendo el flujo en el emulador `Medium_Phone_API_37.0` (Android 17).

| Defecto | Causa raíz | Corrección |
| --- | --- | --- |
| El botón «Añadir» de la hoja del plan se veía cortado como «Añadi». | `OutlineButton` tenía `fillMaxWidth()` forzado y 24 dp de padding horizontal; con el ancho fijo de 92 dp solo quedaban 44 dp para el texto. | `OutlineButton` acepta ahora `contentPadding` y `fillWidth`; la hoja usa `widthIn(min = 88.dp)`, 16 dp de padding y sin relleno. |
| El título decía «Añadir a el desayuno». | Las cadenas `plan_desayuno`, `plan_comida` y `plan_cena` ya traían el artículo («el desayuno»…) y la plantilla era «Añadir a %1$s». | Plantilla «Añadir %1$s» y cadenas «al desayuno», «a la comida», «a la cena». |
| El enlace «Crear cuenta» no hacía nada con los campos vacíos. | El `Text` solo era pulsable con `formularioValido`, así que no llegaba ni a ejecutarse la validación. | Ahora es pulsable siempre y muestra el error de validación correspondiente. |
| Al registrarse, la app parecía no hacer nada. | El proyecto de Supabase exige confirmar el correo (`mailer_autoconfirm = false`), así que `signUpWith` no devuelve sesión y la app se queda en el login. | Se explica el paso de confirmación, se ofrece reenviar el correo y, si la cuenta ya existía, se entra directamente. |
| El reenvío del correo de confirmación a veces fallaba. | Límite de Supabase: `HTTP 429 over_email_send_rate_limit` («solo puedes pedirlo tras 35 segundos»). No es un fallo de la app. | Mensaje explicativo en lugar de un error genérico. |
| El botón «Crear cuenta» dejaba de responder tras pulsar «Continuar con Google». | El acceso con Google abre el navegador y vuelve enseguida, pero el `finally` solo liberaba `cargando` cuando la acción no era de Google: el formulario se quedaba bloqueado en «Entrando…» con todos los controles deshabilitados. | `cargando` se libera siempre en el `finally`, así que el formulario vuelve a responder al regresar del navegador. |
| El enlace «Crear cuenta» costaba pulsarlo y podía quedar bajo la barra de navegación del sistema. | El `Text` tenía un área táctil de unos 20 dp y la actividad usa `enableEdgeToEdge()` sin `navigationBarsPadding()`. | Toda la fila es pulsable (`fillMaxWidth()` y 14 dp verticales) y la columna aplica `navigationBarsPadding()` e `imePadding()`; al aparecer un aviso la pantalla se desplaza arriba automáticamente. |
| Al registrarse por correo, la app mostraba un error genérico y el correo de confirmación no llegaba. | El proyecto tiene activada la confirmación por correo (`mailer_autoconfirm = false`) y el servicio de correo de Supabase limita los envíos: al repetir pruebas responde `HTTP 429` (`over_email_send_rate_limit`). El alta en sí sí se crea (comprobado contra la API). | Se distinguen el límite de envíos y el registro desactivado con mensajes específicos. Para la demo se recomienda desactivar «Confirm email» en Supabase o configurar un SMTP propio. |
| El registro no pedía el nombre de la persona. | El formulario solo tenía correo y contraseña. | Nuevo modo «Crear cuenta» con campo **Nombre** obligatorio. El nombre se guarda en el perfil (`full_name`) al haber sesión y se usa en el saludo de Inicio y en Perfil. |
| Al tocar otro día del calendario de Inicio, la pantalla parpadeaba y perdía la posición del scroll. | `PlanViewModel.estado` emitía el estado «Cargando» dentro del `flatMapLatest`, así que se re-emitía en cada cambio de día y `ContenidoConEstado` sustituía todo el contenido por el indicador de carga. | Se emite «Cargando» solo en la primera carga; mientras llegan los datos del día nuevo se mantiene el día anterior en pantalla. El día resaltado se mueve de inmediato. |
| Los macros de las recetas no coincidían con los calculados a partir de sus ingredientes. | Los hidratos y las grasas se leían del JSON con `getInt`, que trunca los decimales, y `MacrosAlimento` los guardaba como `Int`, así que se perdía la parte decimal. Además el redondeo de Python no coincide con el `roundToInt()` de Kotlin en los empates. | Macros en `Double` leídos con `optDouble`, redondeo `roundToInt()` (half up) y recálculo del catálogo con el mismo criterio. |
| En el detalle de receta, el badge salía «Lista» y con todos los ingredientes marcados aunque faltara alguno. | El badge y la marca de cada línea se calculaban con `alimento != null`, pero `alimento` es la fila del catálogo unida a la receta: existe aunque el alimento no esté en la despensa. | Nueva función `ingredienteDisponible` (básicos, `enDespensa` y cantidad suficiente) que usan ahora el badge y cada línea del detalle. |
| Al añadir un alimento que ya estaba se creaba una fila duplicada («atun» y «Atún» eran el mismo). | La comparación era `equals(ignoreCase)`, que no tiene en cuenta las tildes. | Los nombres se comparan normalizados (sin tildes y en minúsculas); si ya existe, se actualiza la cantidad en lugar de insertar otra fila. |
| El icono de carne y el de pescado eran el mismo, y el salmón salía con el icono de especias. | `categoriaDeIngrediente` mezclaba carne y pescado en una sola categoría y la rama de especias comprobaba «sal» antes que el nombre completo. | Categorías PESCADO y CARNE separadas y comprobadas antes que las especias; pescado con `SetMeal` (el que ya tenía) y carne con el icono de kebab que eligió María. |
| En el «Wrap de pollo», 5 g de aceite de la receta salían como que faltaba si en la despensa el aceite estaba en ml. | `cantidadSuficiente` exigía la misma familia de unidad: masa y volumen no se equiparaban. | La masa y el volumen se equiparan al comprobar cantidades (1 ml ≈ 1 g); la conversión exacta de g/kg y ml/l se mantiene. |
| La despensa mostraba 1000 g en lugar de 1 kg (y 1000 ml en lugar de 1 l). | Las cantidades se mostraban tal cual se guardan en la base de datos. | Nueva `cantidadPresentacion`: a partir de 1000, «g» pasa a «kg» y «ml» a «l» en la lista y en el formulario de edición. |
| En «Marcar como cocinada», el botón «Confirmar sin foto» se veía cortado como «Confirmar sin». | `PrimaryButton` tenía 24 dp de padding fijo y el texto de una línea no cabía en el ancho disponible. | `PrimaryButton` acepta `contentPadding` y la pantalla de captura usa 8 dp; el texto se ve completo. |
| El «Wok de pollo con verduras» no cuadraba: sus macros no coincían con sus ingredientes y sus pasos describían otra receta. | La receta se añadió con las cifras escritas a mano y después sus pasos se reescribieron con los de otra receta. | Macros recalculados con la misma función del catálogo (2093 kcal, 150 g de proteínas, 228 g de hidratos y 62 g de grasas) y pasos reescritos para describir sus ingredientes. |
| La fuente de la app se veía superada por la del móvil y los pasos del «paso a paso» empezaban en minúscula (lo reportaron las personas de prueba con la APK). | La tipografía venía de Google Fonts en línea y los pasos se mostraban tal cual estaban en el JSON. | Nunito empaquetada en `res/font` con prioridad propia y los pasos capitalizados al mostrarse. |

Verificación tras la corrección, en el emulador: registro con una cuenta ya
existente (entra directamente en la app), registro con una cuenta nueva (aviso de
confirmación y enlace de reenvío), login normal, y hoja del plan con el título
«Añadir al desayuno» y el botón «Añadir» completo. Con la confirmación por correo
desactivada en Supabase, el registro con una cuenta nueva entra directamente y el
saludo de Inicio usa el nombre indicado («Hola, Prueba»).

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
- La prueba con personas ajenas al equipo ya se ha hecho: cuatro personas
  probaron la APK que les pasó María y sus observaciones están recogidas en la
  tabla anterior, con la corrección correspondiente.