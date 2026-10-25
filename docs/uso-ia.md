# Registro de uso de IA

Herramientas: Claude y OpenCode con el modelo MiMo-v2.6, asistentes de IA, junto con la documentación oficial de Android y de GitHub.

| Fecha | Para qué se usó | Resultado incorporado | Qué cambiamos | Cómo lo verificamos |
|---|---|---|---|---|
| 20–25/10/2026 | Organizar el proyecto en GitHub: issues, columnas del Kanban, plantillas y protección de `main` | Lista de issues #1 a #13, flujo de ramas, plantillas de issue y de PR | Reparto de responsables según quién hizo cada tarea; títulos sin número | Creado en GitHub; PR #14 y #15 revisadas y aprobadas por la otra persona |
| 21/10/2026 | Guía para crear el proyecto Android y la estructura MVVM | Proyecto base y paquetes `data`, `domain/model` y `ui` | Ajuste manual de la versión de Android en `build.gradle.kts` | Compila y se ejecuta en el emulador; PR #16 y PR #19, ambas aprobadas por la otra persona |
| 25/10/2026 | Definir y dimensionar las issues del proyecto, revisándolas de forma continua | Issues #1 a #13 y #20 a #26 con descripción, responsable, criterios de terminado y clasificación (RA, tamaño, prioridad, persona revisora y sprint) | Al no tener experiencia previa en proyectos, el reparto y los tamaños van variando según vamos aprendiendo; con la ayuda de Claude y OpenCode con el modelo MiMo-v2.6 creamos las issues que nos parecen realistas y consultamos con la IA como guía lo que no dominamos, apoyándonos también en la documentación oficial de Android | Revisión de ambos integrantes: cada issue se comenta, se reasigna y se modifica en su propia página, y su historial de edición y las PR recogen los cambios; el objetivo es aprender mientras hacemos el proyecto |
| 25/10/2026 | Redactar la plantilla de tareas en formato `tarea.yml` (issue form de GitHub), un formato que no conocíamos, con el asistente MiMo | `.github/ISSUE_TEMPLATE/tarea.yml`, con RA, tamaño, prioridad, persona revisora, sprint, criterios y prueba prevista como campos obligatorios | Sustituimos la antigua plantilla `tarea.md` por un formulario y decidimos qué campos eran obligatorios según los criterios de la rúbrica | Revisión del YAML contra la documentación de GitHub sobre issue forms y comprobación de que los campos obligatorios coinciden con lo que pide la Entrega 1 |

No se han introducido claves, credenciales ni datos personales en el asistente.
