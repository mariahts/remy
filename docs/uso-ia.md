# Registro de uso de IA

Herramientas: Claude y OpenCode con el modelo MiMo-v2.6, junto con la documentación oficial de Android y de GitHub.

| Fecha | Para qué se usó | Resultado incorporado | Qué cambiamos | Cómo lo verificamos |
|---|---|---|---|---|
| 20–25/10/2026 | Montar el proyecto en GitHub | Issues #1 a #13, tablero Kanban, plantillas y protección de `main` | Ajustamos el reparto y los títulos a mano | Creado en GitHub; PR #14 y #15 aprobadas por la otra persona |
| 21/10/2026 | Guía para crear el proyecto Android con MVVM | Proyecto base y paquetes `data`, `domain/model` y `ui` | Cambiamos a mano la versión de Android en Gradle | Compila y arranca en el emulador; PR #16 y #19 aprobadas |
| 25/10/2026 | Definir y dimensionar las issues | Issues #1 a #13 y #20 a #26 con clasificación y criterios | Tamaños y reparto ajustados según lo que íbamos aprendiendo | Revisión de los dos en cada issue y en su historial |
| 25/10/2026 | Redactar la plantilla `tarea.yml` | Formulario de issue con RA, tamaño, prioridad, revisor y sprint obligatorios | Sustituimos la plantilla `tarea.md` por un formulario | Contraste con la documentación de GitHub sobre issue forms |
| 28/10/2026 | Guía de la issue #7 (Navigation) y la pantalla en negro | Navegación con tres pantallas y BOM de Compose actualizado | Iconos de la barra por iniciales; descartamos los cambios de versión que Android Studio aplicó solo | App en el emulador: se navega entre las tres pantallas |
| 29/10/2026 | Pasar a texto los PDFs del profesor (rúbrica, guía del proyecto, nota y Entrega 2) para tenerlos a mano | Carpeta local `.opencode/skills/rubrica/` con las reglas del proyecto: rúbrica, requisitos de cada entrega y criterios de nota | De los PDFs nos quedamos con lo que hay que cumplir en cada tarea y quitamos lo que se repetía | Cada PDF se contrastó con su texto original; la carpeta carga con `@rubrica` |
| 29/10/2026 | Pulir las issues del sprint 2 y guía de la #22 | Issues #22, #24 y #25 completas y guía paso a paso | Nombre del `.drawio`, dependencias y criterio de CameraX añadidos | Issues releídas por API; tablero sin incoherencias |
| 31/10/2026 | Prototipo de cámara con CameraX (issue #25) | Pantalla con permiso, vista previa y captura; PR #54 | Elegimos CameraX y dejamos `GetContent` de respaldo | Compila y probada en el emulador |
| 31/10/2026 | Etiquetar issues y preparar la Entrega 2 | Etiquetas en las 40 issues, memoria v2, tablero exportado y `.drawio` en `dev` | Etiquetas sacadas del cuerpo de cada issue; en la memoria se actualizó el estado de #7, #11, #22 y #25 | Tablero releído: 0 issues sin clasificar; el documento abre con sus 7 tablas |

No se han introducido claves, credenciales ni datos personales en el asistente.
