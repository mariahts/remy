# Estado del MVP de REMY

Actualizado a 2026-11-11 para la rama `feature/limpieza-mvp` (trabajo de limpieza, tests, refactor y macros sobre la base auditada de `feature/rediseno-ui`, HEAD `119a202`).

## 1. Resumen

El núcleo funcional del MVP sigue operativo (despensa en Room, catálogo de 20 recetas precargadas, sugerencias con cruce y filtros, detalle/cocción con foto y descuento, planificación semanal, bienvenida y estados vacíos). En esta rama se completaron los bloques de limpieza/tests/refactor/macros definidos en el encargo: descuento extraído y testeado con aviso al usuario, ampliación de tests del cruce, externalización de textos/colores y retirada de recordatorios no implementados, documentación de pruebas alineada con tests existentes, división de `RecetaScreen.kt`, y soporte de macros completas (proteínas, hidratos y grasas) con migración Room 7→8, catálogo estimado, filtros por ración y visualización en detalle.

Pendientes principales fuera de este encargo: verificación manual en dispositivo/emulador (rotación, permiso de cámara, foto/cocción y descuento en flujo real, actualización sobre instalación previa con datos), ejecución instrumentada completa (`connectedDebugAndroidTest`) para validar migración/DAOs/UI en emulador, y decisiones de producto/publicación ya identificadas en la auditoría base (login Supabase para MVP, alcance de favoritas/caducidad, minify/licencias de recursos).

## 2. Compilación y tests

| Comando | Resultado | Evidencia |
|---|---|---|
| `./gradlew :app:assembleDebug` | **BUILD SUCCESSFUL** | Ejecutado en sesión tras cada tarea de esta rama. |
| `./gradlew :app:testDebugUnitTest` | **BUILD SUCCESSFUL** | Ejecutado en sesión tras cada tarea de esta rama. |
| `./gradlew :app:lintDebug` | No reejecutado en esta sesión | Ver auditoría base si se requiere reporte actualizado. |
| `./gradlew :app:connectedDebugAndroidTest` | No reejecutado en esta sesión | Requiere emulador/dispositivo; recomendado para validar migración y suites instrumentadas. |

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
| 3.2 Catálogo | Resuelto | `assets/recetas.json` con hidratos/grasas estimados (20 recetas); precarga actualiza catálogo existente por nombre conservando fecha/foto de cocción; nota `docs/macros-recetas.md` (valores estimados, no oficiales). |
| 3.3 Filtros | Resuelto | Filtros por ración `Alta en proteína` (≥25 g), `Baja en hidratos` (≤30 g), `Baja en grasa` (≤15 g) en `FiltrosSugerencias`/`FiltroRecetas`, chips en sheet, persistidos en DataStore y recuperables, `Limpiar` los reinicia; tests en `FiltroRecetasTest` y `PreferenciasRepositoryTest`. |
| 3.4 Interfaz detalle | Resuelto | Detalle muestra Proteína, Hidratos y Grasas por ración escalados con selector de raciones, junto a kcal (`strings.xml`). |

## 4. Checklist v1 (estado resumido tras esta rama)

| Punto | Estado | Nota |
|---|---|---|
| A1 Despensa CRUD y persistencia | [x] | Sin cambios funcionales de alcance en esta rama. |
| A2 Catálogo ≥20 recetas con tiempo, kcal, macros, tipo, región, ingredientes y pasos | [x] | Macros completas (proteína, hidratos, grasas) añadidas con valores estimados (ver `docs/macros-recetas.md`). |
| A3 Cruce “ya” vs “casi cocinables”, máximo configurable, cantidades/unidades | [~] | Lógica existente; tests ampliados (insuficiente, g/kg, ml/l). Cobertura de integración/UI no revalidada manualmente en esta sesión. |
| A4 Básicos no cuentan como faltantes | [x] | Cubierto por lógica y test de cruce añadido. |
| A5 Filtros (tiempo, calorías, macros, tipo, región) persistidos en DataStore | [x] | Incluye proteínas mínimas, kcal máximas y filtros por ración de macros (alta proteína, baja hidratos, baja grasa), persistidos y recuperables. |
| A6 Cocinar: fecha/foto, permisos, descuento e insuficiente/denegado | [~] | Descuento puro testeado y aviso de no descontados; flujo cámara/galería existente. Prueba manual de foto/permiso/actualización real pendiente. |
| A7 Ajuste de raciones y descuento escalado | [x] | Lógica existente; descuento puro cubierto por tests (incluye escalado). |
| A8 Plan de comidas persistido | [x] | Sin cambios. |
| A9 Bienvenida y estados vacíos | [x] | Sin cambios. |

## 5. Pendiente / no verificado

- **Manual en emulador/dispositivo:** rotación y recreación de estado, permiso de cámara denegado/aceptado, flujo completo “marcar como cocinada” con foto, y validación visual del refactor de Recetas/Detalle/Captura.
- **Instrumentadas:** ejecutar `./gradlew connectedDebugAndroidTest` (incluye `Migration7To8Test`, DAOs y UI) con emulador abierto.
- **Actualización real:** instalar versión previa con datos (despensa, receta cocinada con foto, plan) y actualizar a esta versión; comprobar que se conservan todos los datos y que el catálogo se sincroniza sin duplicados.
- **Revisión de catálogo:** comprobar muestra de recetas en `assets/recetas.json` frente a estimaciones razonables (valores estimados, no oficiales).
- **Producto/publicación (auditoría base):** estrategia de login Supabase para MVP, alcance de favoritas/caducidad/recordatorios fuera de v1, minify/ProGuard en release y licencias/derechos de recursos del catálogo.

## 6. Notas

- `docs/pruebas.md` describe las suites existentes; en esta sesión se verificaron `testDebugUnitTest` y `assembleDebug`.
- Macros de hidratos/grasas del catálogo: **valores estimados, revisar con fuente fiable antes de publicar** (no datos oficiales), ver `docs/macros-recetas.md`.
- No se añadieron dependencias nuevas ni se modificó `gradle/wrapper/gradle-wrapper.properties`. No se realizó `git push`.
