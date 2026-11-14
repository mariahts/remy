# LÉEME — Entrega 4 (REMY)

Documento breve de la versión candidata para la Entrega 4.

## Equipo y enlaces

- **Proyecto:** REMY
- **Repositorio:** https://github.com/mariahts/remy
- **Tablero (GitHub Project):** https://github.com/users/mariahts/projects/1

## Qué es REMY

REMY es una aplicación Android para cocinar con lo que ya tienes en casa: gestiona la despensa, sugiere recetas según los ingredientes disponibles, permite planificar comidas y registrar lo cocinado.

## Contenido de la entrega

- **APK (versión candidata):** `REMY_MARIA_ENTREGA4.apk` (en la carpeta `entrega/` del repositorio, si corresponde a esta entrega).
- **Documentación principal:**
  - `README.md`: descripción pública del proyecto, instalación y ejecución.
  - `docs/entrega-4.md`: índice de entregables y estado.
  - `docs/pruebas.md`: pruebas realizadas, resultados, errores detectados y pendientes.
  - `docs/revision-diseno-usabilidad.md`: revisión de diseño, usabilidad y accesibilidad.
  - `docs/evidencias-individuales.md`: evidencias individuales de contribución.
  - `docs/uso-ia.md`: registro de uso de IA.

## Cómo probar la app

1. Instalar la APK en un dispositivo Android (Android 8.0 / API 26 o superior):
   ```bash
   adb install -r REMY_MARIA_ENTREGA4.apk
   ```
2. Abrir la app y realizar un recorrido básico:
   - Revisar/añadir alimentos en **Despensa**.
   - Consultar **Recetas** y ver el detalle.
   - Comprobar **Sugerencias** según la despensa y aplicar filtros.
   - Usar **Inicio/Plan** para organizar comidas.
   - Marcar una receta como cocinada (con o sin foto) y comprobar el descuento en despensa.

> Si se utiliza una cuenta de demostración, sus credenciales deben proporcionarse por el canal de entrega acordado.

## Pruebas y calidad

- Las pruebas y resultados (incluidos defectos encontrados y correcciones/pendientes) están documentados en `docs/pruebas.md`.
- La revisión de diseño, usabilidad y accesibilidad está en `docs/revision-diseno-usabilidad.md`.

## Notas

- La app almacena los datos de uso (despensa, plan, historial, etc.) localmente en el dispositivo.
- La configuración de autenticación usa una clave pública de cliente incluida en el proyecto; no se incluyen claves secretas en el repositorio.
- Para la entrega definitiva se revisarán y actualizarán, si procede, la memoria en Word, el tablero, las evidencias individuales, las capturas y los artefactos (APK/ZIP) para que reflejen la versión final.
