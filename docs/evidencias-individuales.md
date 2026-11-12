# Evidencias individuales de contribución

Entregable 7 de la Entrega 4. Datos recogidos de GitHub el 12 de noviembre de
2026.

- **María José Sánchez León** — [mariahts](https://github.com/mariahts)
- **Mario Jiménez Ruiz** — [FedericoCamposMartinez](https://github.com/FedericoCamposMartinez)

## Resumen

| | María | Mario |
|---|---|---|
| Commits | 185 | 28 |
| Pull Requests | 52 | 4 (#52, #64, #65, #66) |
| Issues de las que es revisora | 27 | 13 |
| Pull Requests revisadas por la otra persona | 13 | 3 |

Reparto de responsabilidades: María lleva el grueso del desarrollo en Kotlin
—datos, pantallas, pruebas, documentación y empaquetado— y Mario lleva la
parte de diseño de interfaz (maqueta en Stitch, identidad visual y modelo de
datos) y la revisión de las PR.

## Issues

Hay 40 issues en el repositorio. Todas llevan en su cuerpo el bloque
«Clasificación» con RA, tamaño, prioridad, revisor, sprint y dependencias, y
criterios de aceptación comprobables. Todas están cerradas salvo tres:

| Issue | Título | Estado |
|---|---|---|
| #40 | README final y cierre de `docs/uso-ia.md` | Abierta (en revisión) |
| #41 | Memoria final | Abierta, en Blocked (depende de #40) |
| #42 | Presentación de la defensa | Abierta, en Blocked (depende de #41) |

El revisor de cada issue es la persona que la implementó. Las 13 issues cuyo
revisor es María (#8, #9, #10, #11, #12, #13, #36, #39, #40, #41, #43, #44 y
#47) las implementó Mario; las otras 27, María.

## Pull Requests

56 Pull Requests en total: 55 fusionadas y una abierta (#94, `dev` → `main`,
pendiente de la aprobación que exige la protección de `main`).

**De Mario** (4):

| PR | Título |
|---|---|
| #52 | Modelo Entidad/Relación |
| #64 | Aplicar identidad visual propia a la aplicación |
| #65 | Sprint 2 y 3: integración de todo el avance en `main` |
| #66 | Pantallas designed en Stitch |

**De María** (52), agrupadas por bloque de trabajo:

| Bloque | Pull Requests |
|---|---|
| Montaje del proyecto, README, plantillas y esqueleto MVVM | #14, #15, #16, #17, #18, #19, #21, #27 |
| Navegación, documentación y Sprint 2 (despensa, cámara, DataStore) | #49, #50, #51, #53, #54, #55, #56, #57, #58 |
| Sprint 3: catálogo, listado, detalle y sugerencias | #59, #60, #61, #62, #63, #67 |
| Cocción con foto, galería, navegación e historial | #68, #69, #70, #71, #72, #73, #74, #75, #76, #77, #78, #79, #80, #81 |
| Sprint 4: limpieza, macros, rediseño, release y cierre | #82, #83, #84, #85, #86, #87, #88, #89, #90, #91, #92, #93, #94, #95, #96 |

## Issues que menciona cada Pull Request

Relación objective extraída del cuerpo de cada PR (las issues #1 a #50; a
partir de la #51 las PR citan issues de la entrega anterior o no citan ninguna,
porque las tareas ya no se relacionaban con una issue abierta).

| PR | Issues citadas |
|---|---|
| #14 | #2, #3 |
| #15 | #4 |
| #16 | #5 |
| #19 | #6 |
| #21 | #20 |
| #27 | #12, #13 |
| #50, #51 | #7 |
| #52 | #11 |
| #53 | #11, #22, #24 |
| #54 | #25, #35 |
| #55 | #7, #8, #9, #10, #22, #23, #25, #26 |
| #56 | #24, #28 |
| #57 | #11, #23 |
| #58 | #29 |
| #59 | #30 |
| #60 | #31 |
| #61 | #32 |
| #62 | #33 |
| #63 | #29, #33, #34 |
| #64, #66 | #8 |
| #67 | #8, #30 |
| #68 | #25, #35 |
| #69 | #26 |
| #70 | #8, #24, #26, #30, #31, #32, #33, #34, #35 |
| #71 | #24, #30, #34 |
| #72 | #26, #35 |
| #74 | #44 |
| #75 | #44, #45 |
| #76 | #46 |
| #77 | #37 |
| #78 | #38 |
| #85 | #37, #38, #44, #46 |
| #87 | #41, #42 |
| #90 | #40 |

## Revisiones

16 Pull Requests tienen revisión registrada y **las 16 fueron revisadas por la
otra persona**, con aprobación explícita en 15:

| Revisor | PR revisadas |
|---|---|
| FedericoCamposMartinez | #14, #15, #16, #18, #19, #21, #27, #49, #54, #55, #70, #71, #85, #92 |
| mariahts | #52, #65 |

**Pendiente de proceso:** las PR #93, #95 y #96 están fusionadas en `dev` sin
revisión registrada, porque la regla de protección de `dev` no la exige. Antes
de la entrega definitiva conviene que Mario deje su revisión en esas tres y
apruebe la #94, que es la que actualiza `main`.

## Seguimiento semanal

| Sprint | Periodo | Qué se revisó |
|---|---|---|
| 1 | 06/10 – 20/10/2026 | Propuesta, alcance y reparto de las 13 primeras issues |
| 2 | 20/10 – 03/11/2026 | Esqueleto de la app, tema, navegación y primera issue de datos |
| 3 | 03/11 – 10/11/2026 | Integración de datos y función multimedia, con las primeras pruebas instrumentadas |
| 4 | 10/11 – 15/11/2026 | Versión candidata: rediseño, limpieza, macros, fotos y cierre de la entrega |

## Tareas del sprint 4 y sus Pull Requests

| Issue | Tarea | PR |
|---|---|---|
| #34 | Pantalla de sugerencias | #62, #63 |
| #35 | Marcar una receta como cocinada con foto | #68 |
| #36 | Descontar los ingredientes al cocinar | #82 |
| #37 | Filtros de sugerencias con DataStore | #77, #85 |
| #38 | Documentación de pruebas y README | #78, #85 |
| #39 | Empaquetar la APK instalable | #91 |
| #40 | README final y `docs/uso-ia.md` | #87, #90 |
| #41 | Memoria final | #87 |
| #42 | Presentación de la defensa | #87 |
| #43 | Pruebas del algoritmo de sugerencias | #82 |
| #44 | Pantalla de recetas cocinadas | #74, #85 |
| #45 | Navegación completa | #75 |
| #46 | Estados de carga y de error | #76, #85 |
| #47 | Corregir los defectos de las pruebas y refactorizar | #82 |
| #48 | Cierre de la entrega | #89 |

Tres tareas del sprint 4 no se registraron como issue propia y se hicieron
dentro de otras: el arreglo de la hoja del plan y del registro (#93), el
catálogo con macros calculadas y fotos reales (#95) y el parpadeo del
calendario al cambiar de día (#96). Queda por abrir esas tres issues con sus
criterios de aceptación para que la trazabilidad sea completa.
