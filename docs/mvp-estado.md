# Estado del MVP de REMY

Actualizado a 2026-11-11 tras el sprint 4: el trabajo de limpieza, tests,
refactor y macros (rama `feature/limpieza-mvp`, PR #82) y el rediseño de la
interfaz (rama `feature/rediseno-ui`, PR #83) están integrados en `dev` y en
`main` (PR #85).

## 1. Resumen

El núcleo funcional del MVP sigue operativo (despensa en Room, catálogo de 20 recetas precargadas, sugerencias con cruce y filtros, detalle/cocción con foto y descuento, planificación semanal, bienvenida y estados vacíos). En esta rama se completaron los bloques de limpieza/tests/refactor/macros definidos en el encargo: descuento extraído y testeado con aviso al usuario, ampliación de tests del cruce, externalización de textos/colores y retirada de recordatorios no implementados, documentación de pruebas alineada con tests existentes, división de `RecetaScreen.kt`, y soporte de macros completas (proteínas, hidratos y grasas) con migración Room 7→8, catálogo estimado, filtros por ración y visualización en detalle.

Pendientes principales fuera de este encargo: prueba del flujo de foto con la
cámara y la galería en el móvil real (la app se ha probado en un dispositivo
físico y funciona, pero no se ha documentado esa captura con detalle),
actualización sobre una instalación previa con datos, letra grande y gama baja,
y decisiones de producto/publicación ya identificadas en la auditoría base
(alcance de favoritas/caducidad, publicación en tienda).

## 2. Compilación y tests

| Comando | Resultado | Evidencia |
|---|---|---|
| `./gradlew :app:assembleDebug` | **BUILD SUCCESSFUL** | Ejecutado en sesión tras cada tarea de esta rama. |
| `./gradlew :app:testDebugUnitTest` | **BUILD SUCCESSFUL** | Ejecutado en sesión tras cada tarea de esta rama. |
| `./gradlew :app:lintDebug` | No reejecutado en esta sesión | Ver auditoría base si se requiere reporte actualizado. |
| `./gradlew :app:connectedDebugAndroidTest` | **BUILD SUCCESSFUL** | Ejecutado en el emulador `Medium_Phone_API_37.0` (Android 17): 22 pruebas instrumentadas en 6 suites, 0 fallos. |

Suites unitarias presentes (`app/src/test`): `ValidacionAlimentoTest`, `AlgoritmoSugerenciasTest`, `DescuentoDespensaTest`, `FiltroRecetasTest`, `PreferenciasRepositoryTest`, `AlmacenamientoFotosTest`, `FechaEspanolTest`. Suites instrumentadas presentes (`app/src/androidTest`): `AlimentoDaoTest`, `RecetaDaoTest`, `PrecargaRecetasTest`, `Migration7To8Test`, `EstadoCargaUiTest`, `FiltrosSugerenciasUiTest`.

## 3. Trabajo realizado en `feature/limpieza-mvp`

| Bloque/Tarea | Estado | Evidencia principal |
|---|---|---|
| 1.1 Descuento testeable | Resuelto | `logica/DescuentoDespensa.kt` (función pura, devuelve actualizaciones y no descontados), refactor `RecetaDetalleViewModel`, `DescuentoDespensaTest`. |
| 1.2 Aviso al usuario | Resuelto | Aviso tras cocinar con nombres no descontados (`detalle_aviso_descuento`), cocción se guarda igualmente. |
| 1.3 Tests del cruce | Resuelto | Ampliado `AlgoritmoSugerenciasTest` (cantidad insuficiente, conversión g/kg y ml/l, básicos sal/agua/pimienta). |
| 1.4 Strings y colores | Resuelto | Textos de Login/CapturaFoto/ViewModel externalizados a `strings.xml`; `NaranjaAviso` en `ui/theme/Color.kt`. |
| 1.5 Perfil | Resuelto | Eliminado switch "Recordatorios de cocina" y strings asociados. |
| 1.6 Documentación | Resuelto | `docs/pruebas.md` actualizado para citar solo tests existentes. |
| 2 Refactor `RecetaScreen.kt` | Resuelto | Dividido en `RecetasScreen.kt`, `RecetaDetalleScreen.kt`, `CapturaPlato.kt`; eliminado legacy sin referencias (`PillContador`, `PieRecetas`, `ChipIngrediente`, `TarjetaReceta`, `RemyHeader.kt`); sustituidos `!!` en foto/detalle/captura. |
| 3.1 Modelo y BD | Resuelto | `Receta` con `hidratos`/`grasas` (g totales, default 0), Room v8, `MIGRATION_7_8`, esquema `app/schemas/com.remy.app.data.RemyDatabase/8.json`, test `Migration7To8Test`. |
| 3.2 Catálogo | Resuelto | `assets/recetas.json` con 20 recetas y 31 alimentos; hidratos y grasas por 100 g/ml en cada alimento y macros de cada receta **calculadas a partir de sus ingredientes** al precargar (`macrosDeReceta`); la precarga actualiza el catálogo existente por nombre conservando fecha/foto de cocción. |
| 3.3 Filtros | Resuelto | Filtros por ración `Alta en proteína` (≥25 g), `Baja en hidratos` (≤30 g), `Baja en grasa` (≤15 g) en `FiltrosSugerencias`/`FiltroRecetas`, chips en sheet, persistidos en DataStore y recuperables, `Limpiar` los reinicia; tests en `FiltroRecetasTest` y `PreferenciasRepositoryTest`. |
| 3.4 Interfaz detalle | Resuelto | Detalle muestra Proteína, Hidratos y Grasas por ración escalados con selector de raciones, junto a kcal (`strings.xml`). |

## 4. Checklist v1 (estado resumido tras esta rama)

| Punto | Estado | Nota |
|---|---|---|
| A1 Despensa CRUD y persistencia | [x] | Sin cambios funcionales de alcance en esta rama. |
| A2 Catálogo ≥20 recetas con tiempo, kcal, macros, tipo, región, ingredientes y pasos | [x] | Macros completas (proteína, hidratos, grasas) calculadas a partir de la tabla nutricional de cada alimento; fotos reales en las 20 recetas. |
| A3 Cruce “ya” vs “casi cocinables”, máximo configurable, cantidades/unidades | [~] | Lógica existente; tests ampliados (insuficiente, g/kg, ml/l). Cobertura de integración/UI no revalidada manualmente en esta sesión. |
| A4 Básicos no cuentan como faltantes | [x] | Cubierto por lógica y test de cruce añadido. |
| A5 Filtros (tiempo, calorías, macros, tipo, región) persistidos en DataStore | [x] | Incluye proteínas mínimas, kcal máximas y filtros por ración de macros (alta proteína, baja hidratos, baja grasa), persistidos y recuperables. |
| A6 Cocinar: fecha/foto, permisos, descuento e insuficiente/denegado | [~] | Descuento puro testeado y aviso de no descontados; flujo cámara/galería existente y probado en un móvil físico. Queda documentar la captura con detalle y la actualización sobre una instalación previa. |
| A7 Ajuste de raciones y descuento escalado | [x] | Lógica existente; descuento puro cubierto por tests (incluye escalado). |
| A8 Plan de comidas persistido | [x] | Sin cambios. |
| A9 Bienvenida y estados vacíos | [x] | Sin cambios. |

## 5. Pendiente / no verificado

- **Manual en dispositivo físico:** la app se ha instalado y probado en un móvil
  real y funciona. En esa prueba aparecieron dos defectos (el alta de cuenta y
  el parpadeo del calendario) que ya están corregidos y verificados. Queda
  documentar con detalle la captura de la foto del plato.
- **Instrumentadas:** ejecutadas y en verde (22 en 6 suites) en el emulador
  `Medium_Phone_API_37.0`, incluidas `Migration7To8Test`, `CatalogoRecetasTest`,
  los DAOs y las de UI.
- **Actualización real:** instalar versión previa con datos (despensa, receta cocinada con foto, plan) y actualizar a esta versión; comprobar que se conservan todos los datos y que el catálogo se sincroniza sin duplicados.
- **Revisión de catálogo:** los macros de cada receta se calculan a partir de sus ingredientes y hay una prueba instrumentada (`CatalogoRecetasTest`) que lo comprueba. Queda contrastarlos con una fuente oficial (por ejemplo, la Base de Datos Española de Composición de Alimentos).
- **Producto/publicación (auditoría base):** estrategia de login Supabase para MVP, alcance de favoritas/caducidad/recordatorios fuera de v1, minify/ProGuard en release y licencias/derechos de recursos del catálogo.

## 6. Notas

- `docs/pruebas.md` describe las suites existentes; en esta sesión se verificaron `testDebugUnitTest` y `assembleDebug` (54 pruebas unitarias en verde) y `connectedDebugAndroidTest` (22 instrumentadas en verde).
- Macros del catálogo: se calculan sumando el aporte de cada ingrediente a partir de los valores nutricionales por 100 g/ml. Son coherentes por construcción, pero la tabla es estándar y está revisada a mano, no extraída de una base de datos oficial.
- No se añadieron dependencias nuevas ni se modificó `gradle/wrapper/gradle-wrapper.properties`.
- La navegación tiene cuatro destinos en la barra inferior (Inicio, Despensa, Recetas y Sugerencias); el Perfil y el detalle de receta se abren desde la cabecera.
