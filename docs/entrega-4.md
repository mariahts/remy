# Entrega 4 — índice de entregables

Cierre: domingo 15 de noviembre de 2026, 23:59. Valor: 5% de la evaluación
continua. Documento actualizado el 12 de noviembre de 2026.

Los ocho entregables que pide el enunciado, dónde está cada uno y qué queda
pendiente. Los enlaces son al repositorio
(https://github.com/mariahts/remy) y al tablero
(https://github.com/users/mariahts/projects/1).

| # | Entregable | Dónde está | Estado |
|---|---|---|---|
| 1 | Enlaces al repositorio y al GitHub Project | Cabecera del [README](../README.md) | Completado |
| 2 | Versión candidata con las funcionalidades principales integradas | `REMY_MARIA_ENTREGA4.apk` (5.490.573 bytes, release con R8 y firma propia) y `REMY_MARIA_ENTREGA4.zip` | Completado |
| 3 | Lista breve de pruebas y resultados, con los errores detectados y cómo se resolvieron | [`pruebas.md`](pruebas.md) | Completado |
| 4 | Revisión del diseño, la usabilidad y la accesibilidad | [`revision-diseno-usabilidad.md`](revision-diseno-usabilidad.md) | Completado |
| 5 | Memoria en Word y `README.md` actualizados | [README](../README.md) y la memoria en Word, entregada en el Aula Virtual | Memoria pendiente (#41) |
| 6 | Tablero Kanban revisado | Project 1: Sprint 4 en curso, Sprint 5 planificado, sprints 1-3 completados, #41 y #42 en Blocked | En revisión final |
| 7 | Evidencias individuales de contribución | [`evidencias-individuales.md`](evidencias-individuales.md) | Completado |
| 8 | Resumen de qué se terminará o corregirá antes de la entrega definitiva | Final de este documento y sección «Pendiente» del README | Completado |

## 2. Versión candidata

`REMY_MARIA_ENTREGA4.apk` (5,2 MB) es la build de `release` con ofuscación R8,
recorte de recursos y firma propia. Incluye todo lo integrado en `dev`:

- Inicio con receta del día, accesos directos y semana planificada.
- Despensa con 12 alimentos de demostración sembrados con las unidades de las
  recetas.
- Catálogo de 20 recetas con foto real, pasos detallados y macros calculadas a
  partir de los ingredientes.
- Sugerencias en dos grupos con filtros completos y persistentes.
- Detalle con macros por ración, descuadre de la despensa al cocinar, foto con
  cámara o galería, registro en el calendario y aviso de ingredientes no
  descontados.
- Inicio de sesión y registro con Supabase.

## 6. Tablero Kanban

- 40 issues, 56 Pull Requests, 213 commits.
- Sprint 1, 2 y 3 completados; Sprint 4 en curso (desde el 10/11/2026) y
  Sprint 5 planificado (desde el 17/11/2026).
- Las 13 issues del sprint 4 tienen asignado el sprint que declara su propio
  cuerpo; #41 y #42 (sprint 5) están en Blocked porque dependen de #40 y de
  ellas mismas.
- Las Pull Requests abiertas y en revisión también están en el tablero.

## 8. Qué se terminará o corregirá antes de la entrega definitiva

- **Fotos del catálogo**: sustituidas por fotografías reales del plato, con
  licencia libre y crédito en [`fotos-recetas.md`](fotos-recetas.md). Queda
  pendiente revisarlas y, si hace falta, sustituir alguna por otra.
- **Memoria en Word** (#41) y **presentación de la defensa** (#42): dependen de
  la revisión de la entrega.
- **Revisión de las últimas Pull Requests**: las PR #93, #95 y #96 están
  fusionadas en `dev` sin revisión registrada, y la #94 (`dev` → `main`) espera
  la aprobación que exige la protección de `main`.
- **Letra grande y gama baja**: la aplicación ya se ha instalado y probado en un
  móvil físico, y funciona. Falta comprobar la fuente ampliada y un equipo de
  gama baja.
- **Prueba con personas ajenas** al equipo: 2-3 usuarios sin explicación.
- **Contraste de los macros** con una fuente de referencia (la Base de Datos
  Española de Composición de Alimentos).
- **Caducidad de la despensa**: requiere una migración de la base de datos, así
  que queda fuera de esta entrega.

## Aviso del enunciado

«Evitad añadir funcionalidades grandes que puedan poner en riesgo la
estabilidad; priorizad que lo implementado funcione y se pueda explicar».

Desde la apertura de esta entrega el trabajo se ha limitado a corregir
defectos, documentar y verificar: el único bloque de funcionalidad añadido son
las fotos del catálogo y el cálculo de macros, que no toca la base de datos.
