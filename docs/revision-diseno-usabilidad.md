# Revisión de diseño, usabilidad y accesibilidad

**Fecha:** 11 de noviembre de 2026
**Alcance:** aplicación REMY (Android, Kotlin + Jetpack Compose) en su versión candidata para la entrega 4.
**Referencia de diseño:** `docs/diseno/REMY-rediseno-app.html` (maqueta de las siete pantallas).

---

## 1. Método

1. Se extrajeron las siete pantallas de la maqueta (`docs/diseno/REMY-rediseno-app.html` es un export de un
   bundler: cada pantalla está comprimida dentro del manifiesto) y se renderizaron en el emulador para
   compararlas a tamaño real con la aplicación.
2. Se instaló la APK candidata en el emulador `Medium_Phone_API_37.0` (Android 17) y se recorrieron las
   siete pantallas con la cuenta de demostración, guardando una captura de cada una.
3. Se compararon: jerarquía, colores, tipografías, radios, espaciados, número de destinos de la barra
   inferior y estados (vacío, carga, error).
4. El contraste de color se calculó con la fórmula de WCAG 2.1 sobre los valores reales del tema
   (`ui/theme/Color.kt`).

Capturas de la aplicación y de la maqueta en `capturas-pantallas/` (app) y `docs/diseno/` (maqueta).

---

## 2. Comparación pantalla a pantalla

| Pantalla | Diseño | Aplicación (antes) | Estado |
|---|---|---|---|
| Barra inferior | 4 destinos: Inicio, Despensa, Recetas, Sugerencias. Icono dentro de un círculo azul tinta en el destino activo, con la etiqueta debajo. | 3 destinos (Sugerencias se abría desde una tarjeta de Inicio) y los iconos en horizontal, con la etiqueta a la derecha. | **Corregido** |
| Recetas | Miniaturas con bloque de color pastel sólido (melocotón, mostaza, azul) y la etiqueta de tiempo abajo. | Miniatura beige con el icono de un tenedor y cuchillo dentro. | **Corregido** |
| Sugerencias | Destino de la barra, con avatar arriba a la derecha y sin flecha de volver. | Pantalla secundaria con flecha de volver. | **Corregido** |
| Inicio | Tarjeta «Tu receta de hoy» con la insignia «Lista para cocinar». | La insignia era fija: se mostraba «Lista para cocinar» incluso con ingredientes faltantes. | **Corregido** |
| Detalle de receta | Título 26 px, insignias de tiempo/kcal/raciones, lista de ingredientes con avatar y cantidad, botón «Empezar a cocinar». | Título, insignias de tiempo/kcal/proteína, selector de raciones, ingredientes con cantidad por ración y botón «Marcar como cocinada». | Mejora de la app (macros y raciones) |
| Despensa | Agrupación en «Revísalos primero» (caducidad) y «El resto». | Lista única, sin caducidad. | **Pendiente**: el modelo `Alimento` no tiene fecha de caducidad (requeriría una migración de base de datos) |
| Login | Logo REMY, campos con icono, botón terracota «Entrar a mi cocina», «Continuar con Google», enlaces «Crear cuenta» y «Probar con la cuenta de demo». | Estructura equivalente. | El enlace a la cuenta de demo se ha retirado (issue #36 y PR #86) |
| Perfil | Inicial, correo, tres métricas, favoritas, ajustes y «Cerrar sesión». | Idéntica. | Correcto |
| Inicio, semana | Etiqueta «noviembre» junto a «Tu semana». | Sin la etiqueta del mes. | **Corregido** |
| Inicio, accesos directos | Las dos tarjetas de «Añadir a mi despensa» y «Ver sugerencias» miden lo mismo. | La tarjeta de «Ver sugerencias» era más alta porque solo una llevaba subtítulo, y quedaban los bordes desalineados. | **Corregido** |

---

## 3. Cambios aplicados en esta revisión

| Commit | Cambio |
|---|---|
| `9130990` | Las miniaturas de receta usan bloques de color pastel, repartidos de forma estable según el nombre de la receta (`logica/ColorReceta.kt`). |
| `e33ca05` | Sugerencias pasa a ser el cuarto destino de la barra inferior, con el icono dentro de un círculo y la etiqueta debajo. |
| `2421ed3` | La tarjeta «Tu receta de hoy» indica si la receta está lista para cocinar o cuántos ingredientes le faltan. |
| `5ba7ebd` | La despensa de la cuenta de demostración se siembra con las mismas unidades que usan las recetas (gramos y mililitros). |
| `8fd900a` | Los básicos de la bienvenida y el formulario de alta usan también esas unidades. |
| `a4db655` | El botón principal deshabilitado mantiene el tono terracota en lugar de desvanecerse. |
| `320326d` | La cabecera «Tu semana» muestra el mes, como en la maqueta. |
| `be9282f` | Cada macronutriente tiene su propio icono en el detalle de la receta. |
| `ba62063` | Los dos accesos directos del inicio («Añadir a mi despensa» y «Ver sugerencias») tienen la misma altura, y el de la despensa indica cuántos alimentos hay guardados. |

El cambio de unidades corrige un defecto real: antes la despensa de demostración se sembraba con
`Cebolla 3 ud`, `Huevo 6 ud`, `Patata 4 ud` y `Pasta`, que no existe en el catálogo. Como el algoritmo de
sugerencias compara cantidad y unidad, ninguna receta llegaba a considerarse completa y la aplicación
mostraba «0 listas para cocinar». Con la corrección, la cuenta de demostración muestra tres recetas
listas para cocinar («Pasta con tomate», «Pollo al curry» y «Tortilla de patatas»), igual que en la maqueta.

Las capturas de esta revisión se han hecho con la versión de entrega (`assembleRelease`, con ofuscación
y firma) instalada en el emulador, de modo que muestran exactamente la aplicación que se entrega.

---

## 4. Accesibilidad

### 4.1 Contraste (WCAG 2.1, calculado sobre el tema real)

| Combinación | Ratio | Nivel |
|---|---|---|
| Texto principal (#14213D) sobre fondo crema (#FFFBF5) | 15,49 | AAA |
| Texto secundario (#475569) sobre fondo crema | 7,35 | AAA |
| Texto secundario sobre superficie blanca | 7,58 | AAA |
| Blanco sobre terracota (#C2410C), botones principales | 5,18 | AA |
| Blanco sobre azul tinta (#14213D), destino activo | 15,97 | AAA |
| Texto principal sobre melocotón (#FFEDD5) | 13,94 | AAA |
| Aviso de falta (#9A3412 sobre #FFEDD5) | 6,38 | AA |
| Aviso de caducidad (#991B1B sobre #FEE2E2) | 6,80 | AA |
| Aviso neutro (#334155 sobre #E8EEF3) | 8,85 | AAA |
| Texto sobre cualquiera de los seis tonos pastel | entre 7,54 y 12,77 | AAA |

No hay ninguna combinación por debajo del nivel AA.

### 4.2 Descripciones para lectores de pantalla

Los iconos con acción llevan `contentDescription` con el nombre de la acción; los iconos decorativos
(avatar del logotipo, separadores, iconos de sección) lo tienen a `null`, que es lo correcto para que el
lector no los anuncie. Las etiquetas de la barra inferior usan el nombre del destino como descripción.

### 4.3 Tamaño de las zonas táctiles

Los destinos de la barra inferior miden 40 dp de círculo con la etiqueta debajo (el área completa supera
los 48 dp recomendados), los botones principales 48 dp de alto y los iconos de las cabeceras 40 dp con
margen. Se puede mejorar en una iteración posterior.

### 4.4 Escalado de fuente

Los textos usan unidades `sp` y siguen el escalado del sistema, pero hay elementos con altura fija
(por ejemplo, las tarjetas de acceso directo de Inicio, de 88 dp) que podrían recortar el texto con la
letra muy grande. **Pendiente**: comprobar con la fuente al 150 % y al 200 % y convertir las alturas
fijas en alturas mínimas.

### 4.5 Modo oscuro

La maqueta solo define el modo claro, así que la aplicación no lo implementa. No es un defecto respecto
al diseño, pero es una mejora pendiente.

---

## 5. Usabilidad

- **Recorrido principal verificado** en el emulador: inicio de sesión, inicio, despensa, recetas,
  detalle, sugerencias, filtros y perfil.
- **Estados vacíos**: inicio, despensa, recetas y sugerencias tienen su propio mensaje cuando no hay
  datos, con acción para resolverlo.
- **Filtros de sugerencias**: se guardan en DataStore y se recuperan al reabrir la aplicación
  (comprobado en el emulador: el filtro «Alta en proteína» se mantiene tras cerrar y abrir la app).
- **Aviso de ingredientes no descontados**: al cocinar se informa de qué ingredientes no se pudieron
  descontar de la despensa y la cocción se guarda igualmente.
- **Coherencia de los mensajes**: la tarjeta de Inicio, la lista de sugerencias y el detalle de receta
  usan la misma disponibilidad de la receta (lista o «faltan N»).

---

## 6. Qué queda pendiente

| Punto | Por qué no se ha hecho |
|---|---|
| Fotos reales de las recetas | Requiere 20 imágenes propias o con licencia libre; es trabajo humano. |
| Caducidad de los alimentos en la despensa | El modelo `Alimento` no tiene ese campo; añadirlo obliga a una migración de base de datos (Room v9) y a la revisión de la categoría de la arroba. |
| Escalado de fuente al 150 % y al 200 % | Falta revisar las alturas fijas de algunos componentes. |
| Modo oscuro | No está en la maqueta; se deja para una versión posterior. |
| Pruebas en móviles reales y con personas ajenas | Necesitan dispositivos y usuarios fuera del equipo. |

---

## 7. Cómo reproducir esta revisión

1. Compilar y generar la APK: `./gradlew :app:assembleDebug`.
2. Instalar en el emulador `Medium_Phone_API_37.0` con `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
3. Entrar con la cuenta de demostración (`demo@remy.app` / `Demo1234!`) y recorrer las siete pantallas.
4. Para ver la maqueta, abrir `docs/diseno/REMY-rediseno-app.html` en un navegador a 390 x 844 px.
