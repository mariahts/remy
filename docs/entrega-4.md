# Entrega 4 — índice de entregables

Cierre: domingo 15 de noviembre de 2026, 23:59.  
Documento de entrega (versión candidata): actualizado para reflejar el estado actual del repositorio.

- **Repositorio:** https://github.com/mariahts/remy
- **Tablero (GitHub Project):** https://github.com/users/mariahts/projects/1

## Entregables

| # | Entregable | Ubicación / evidencia | Estado |
|---|---|---|---|
| 1 | Enlaces al repositorio y al GitHub Project | Este documento y [`README.md`](../README.md) | Completado |
| 2 | Versión candidata con funcionalidades principales integradas | APK en [`entrega/REMY_MARIA_ENTREGA4.apk`](../entrega/REMY_MARIA_ENTREGA4.apk) (si se regenera, actualizar el artefacto y este documento) | Completado (artefacto en repo/entrega) |
| 3 | Lista breve de pruebas realizadas y resultados (errores detectados y resolución/pendientes) | [`docs/pruebas.md`](pruebas.md) | Completado |
| 4 | Revisión de diseño, usabilidad y accesibilidad | [`docs/revision-diseno-usabilidad.md`](revision-diseno-usabilidad.md) | Completado |
| 5 | Memoria en Word y `README.md` actualizados | [`README.md`](../README.md) actualizado; memoria Word se entrega en Aula Virtual | Memoria Word: pendiente de entrega en Aula Virtual (según corresponda) |
| 6 | Tablero Kanban revisado | [GitHub Project 1](https://github.com/users/mariahts/projects/1) (tareas completadas, pendientes y descartadas identificadas) | En revisión/seguimiento en tablero |
| 7 | Evidencias individuales de contribución | [`docs/evidencias-individuales.md`](evidencias-individuales.md) y trazabilidad en Issues/PRs/commits del repositorio | Completado (documento) |
| 8 | Resumen de qué se terminará o corregirá antes de la entrega definitiva | Sección [“Pendiente antes de la entrega definitiva”](#pendiente-antes-de-la-entrega-definitiva) | Completado |

## Resumen de la versión candidata

- Catálogo de recetas e ingredientes precargado desde `app/src/main/assets/recetas.json`, con sugerencias según la despensa, filtros, planificación semanal e historial/cocción con foto.
- **Catálogo actual:** 25 recetas (se actualizaron los pasos de las recetas a partir de los `.txt` de la fuente de trabajo facilitada y se eliminó “Bacalao al pil-pil”).
- Datos locales con Room (migraciones y esquemas en `app/schemas/`), preferencias con DataStore y foto con CameraX/Coil.
- Inicio de sesión/registro con Supabase (clave pública de cliente incluida para ejecución del proyecto).

> Nota: el aviso del enunciado indica evitar añadir funcionalidades grandes a partir de esta entrega y priorizar estabilidad, corrección, pruebas y documentación.

## Pruebas y verificación

- Detalle de pruebas, entorno, resultados, defectos y pendientes: [`docs/pruebas.md`](pruebas.md).
- En el trabajo reciente se verificó compilación y pruebas unitarias/ensamblado de debug en verde (`./gradlew :app:testDebugUnitTest :app:assembleDebug`) tras los últimos cambios de catálogo/pasos.

## Seguridad y datos

- No se han incluido credenciales secretas en el repositorio. La clave de Supabase presente en el código es la **clave pública (publishable/anon) de cliente**, pensada para uso en la app; la clave secreta no forma parte del repositorio.
- Datos de la app (despensa, plan, historial, etc.) se almacenan localmente en el dispositivo (Room).
- Las cuentas de demostración y correos de prueba, si se usan para revisión, deben gestionarse/revocarse fuera del repositorio cuando ya no sean necesarios.

## Pendiente antes de la entrega definitiva

- **Memoria en Word** y **presentación/defensa**: completar y entregar según calendario del módulo.
- **Revisión final del tablero**: asegurar que todas las tareas del hito estén correctamente clasificadas (completadas, pendientes, descartadas/bloqueadas) y con trazabilidad.
- **Evidencias individuales**: mantener Issues/PRs/commits y revisiones al día; revisar que toda aportación relevante quede trazada.
- **Regenerar artefactos de entrega si cambia el código**: APK/ZIP y capturas deben corresponder a la versión candidata final que se entregue.
- **Pruebas adicionales recomendadas**: comprobación con letra grande, en dispositivo físico/gama baja si es posible, y pruebas con personas ajenas sin explicación previa.
- **Revisión de contenidos/documentación**: contrastar macros y datos del catálogo con fuentes de referencia si se requiere mayor rigor, y mantener actualizados README, memoria y documentos de entrega.