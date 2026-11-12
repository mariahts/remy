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
—datos, pantallas, pruebas, documentación y empaquetado— y también el diseño
final de la interfaz. Mario lleva la maqueta de Stitch, el modelo de datos y la
revisión de las PR.

### Diseño de interfaz

El trabajo de diseño tiene dos partes y conviene distinguirlas:

- **Mario** hizo las pantallas maquetadas en Stitch, que son la referencia
  visual de partida, y el modelo Entidad/Relación.
- **María** reorganizó ese diseño para que la identidad quedara más limpia:
  redefinió la jerarquía visual y la paleta (crema, terracota y azul tinta con
  Nunito) sobre las pantallas de Stitch, descartó la paleta verde y la
  tipografía Plus Jakarta Sans de la primera identidad, e implementó el tema
  resultante en Compose.

El resultado está documentado en `docs/disenio/Identidad visual de REMY.md` y
`docs/revision-diseno-usabilidad.md`, y llegó a la app en la PR #83 (rediseño)
y en la #90 (pulido de interfaz).

## Issues

Hay 40 issues en el repositorio. Todas llevan en su cuerpo el bloque
«Clasificación» con responsable, RA, tamaño, prioridad, revisor, sprint y
dependencias, y criterios de aceptación comprobables. Todas están cerradas
salvo tres:

| Issue | Título | Estado |
|---|---|---|
| #40 | README final y cierre de `docs/uso-ia.md` | Abierta (en revisión) |
| #41 | Memoria final | Abierta, en Blocked (depende de #40) |
| #42 | Presentación de la defensa | Abierta, en Blocked (depende de #41) |

**Responsable de todas las issues: María.** La columna «Revisor» recoge quién
revisa el trabajo; en las 13 issues cuyo revisor es María (#8, #9, #10, #11,
#12, #13, #36, #39, #40, #41, #43, #44 y #47) quien aporta el resultado es
Mario: la maqueta de Stitch, el modelo Entidad/Relación, la primera identidad
visual y la revisión de las PR.

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

**De María**: 48 PR de los sprints 1 a 3 (montaje del proyecto, esqueleto
MVVM, despensa, cámara, DataStore, catálogo, navegación, cocina con foto e
historial) y 15 del sprint 4, que son las que cierran esta entrega:

| PR | Título |
|---|---|
| #82 | Limpieza del MVP, refactor de recetas y macros completas |
| #83 | Rediseño de la interfaz y mejoras de experiencia |
| #84 | Actualiza README y registro de uso de IA al cierre de la entrega |
| #85 | Sprint 4: acercándonos a la v1 con el MVP (rediseño, limpieza y macros) |
| #86 | Retira el acceso con la cuenta de demo del login |
| #87 | Actualiza el README y la documentación de pruebas al cierre del sprint 4 |
| #88 | Arregla las pruebas instrumentadas y cubre los filtros de macros |
| #89 | Pulido de interfaz: miniaturas pastel, Sugerencias en la barra y coherencia con la maqueta |
| #90 | Actualiza el README y la documentación de pruebas tras el pulido de interfaz |
| #91 | Ofusca la APK de entrega con R8 y prepara la build de release firmada |
| #92 | Entrega 4: v.1 de REMY con pulido de interfaz, release firmada y documentación al día |
| #93 | Arregla el botón añadir de la hoja del plan y la creación de cuenta |
| #94 | Entrega 4: arreglos de la hoja del plan, el registro y el calendario, y catálogo con fotos reales y macros calculadas |
| #95 | Añade fotos reales al catálogo y calcula los macros de las recetas desde sus ingredientes |
| #96 | Arregla el parpadeo de la pantalla de inicio al cambiar de día |

La relación completa entre PR e issue se consulta en cada PR, que cita las
issues que cierra (`Closes #N`).

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
