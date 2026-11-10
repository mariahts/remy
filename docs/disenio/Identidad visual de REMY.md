# Identidad visual de REMY

Tema de Compose de la aplicación · Issue #8 · Noviembre de 2026

## Resumen

REMY tiene una identidad visual fresca y natural, pensada para una app de despensa y recetas. Se basa en un verde vivo como color de marca sobre un fondo verde menta muy suave, con tarjetas blancas de esquinas muy redondeadas y botones en forma de píldora. Toda la identidad está definida en un único tema de Compose (`REMYTheme`), que envuelve la navegación completa desde `MainActivity`, de modo que cualquier pantalla la hereda sin configuración adicional.

Los valores salen de los bocetos de Despensa, Añadir alimento, Editar cantidad, Eliminar alimento, Recetas guardadas y Sugerencias. Los colores se midieron directamente sobre las imágenes.

## Decisiones generales

- **Solo modo claro.** El equipo ha decidido no incluir tema oscuro. Las barras del sistema se fuerzan a estilo claro en `MainActivity` para que los iconos sean siempre legibles.
- **Sin colores dinámicos.** No se usan los colores del fondo de pantalla de Android 12+, para que la app se vea igual en todos los dispositivos.
- **Un único punto de definición.** Colores, tipografía y formas viven en `ui/theme/` y las pantallas deben usarlos mediante `MaterialTheme`, sin escribir valores a mano.

## Colores

### Verde de marca

| Nombre | Hexadecimal | Uso |
| --- | --- | --- |
| Green600 | `#16A34A` | Color principal: botones, botón flotante, chips seleccionados, pestaña activa |
| Green700 | `#15803D` | Color secundario |
| Green900 | `#14532D` | Texto sobre contenedores verdes claros |
| Green100 | `#DCFCE7` | Contenedores verdes suaves (indicadores, categoría Frescos) |
| Mint | `#E2F8E7` | Fondo de las pantallas |
| MintDeep | `#DCF5E3` | Superficie menta más intensa (hojas inferiores, diálogos) |

El fondo es un degradado vertical de blanco a menta (`RemyBackgroundBrush`), visible en la cabecera de Despensa.

### Neutros

| Nombre | Hexadecimal | Uso |
| --- | --- | --- |
| Blanco | `#FFFFFF` | Tarjetas, barra de navegación y superficies principales |
| Slate900 | `#0F172A` | Texto principal |
| Slate500 | `#64748B` | Texto secundario e iconos inactivos |
| Slate300 | `#CBD5E1` | Bordes |
| Slate200 | `#E2E8F0` | Bordes suaves y separadores |
| Slate100 | `#F1F5F9` | Botón Cancelar y fondos neutros |

### Colores de estado

| Nombre | Hexadecimal | Uso |
| --- | --- | --- |
| Red600 | `#DC2626` | Error y acción de eliminar |
| Red100 | `#FEE2E2` | Fondo de elementos de error |
| Red900 | `#991B1B` | Texto sobre fondos de error |
| Orange600 | `#EA580C` | Avisos ("Usar pronto", "Consumir en 2 días") |
| Orange100 | `#FFEDD5` | Fondo de avisos |
| Orange800 | `#9A3412` | Texto sobre fondos de aviso |
| Indigo700 | `#3730A3` | Texto de chips informativos (kcal, proteína) |
| Indigo100 | `#E0E7FF` | Fondo de chips informativos |

### Colores por categoría de alimento

Se usan para el fondo de los iconos y las etiquetas de cada categoría. Están disponibles como `MaterialTheme.remyColors`.

| Categoría | Hexadecimal |
| --- | --- |
| Cereales | `#FEF08A` |
| Verduras | `#FFE4C8` |
| Proteínas | `#FFEDD5` |
| Frescos | `#DCFCE7` |
| Aceites | `#ECFCCB` |

### Correspondencia con Material 3

| Rol de Material 3 | Valor |
| --- | --- |
| primary / onPrimary | `#16A34A` / blanco |
| primaryContainer / onPrimaryContainer | `#DCFCE7` / `#14532D` |
| secondary | `#15803D` |
| tertiary | `#EA580C` |
| background / onBackground | `#E2F8E7` / `#0F172A` |
| surface / onSurface | blanco / `#0F172A` |
| surfaceVariant / onSurfaceVariant | `#F1F5F9` / `#64748B` |
| outline / outlineVariant | `#CBD5E1` / `#E2E8F0` |
| error / errorContainer | `#DC2626` / `#FEE2E2` |
| surfaceContainerHighest | blanco (así las `Card` se ven blancas) |
| surfaceContainerHigh | `#E2F8E7` (diálogos) |

## Tipografía

Los bocetos usan una sans-serif geométrica y redondeada, que se corresponde con **Plus Jakarta Sans**. Mientras no se añadan los archivos de la fuente al proyecto (`res/font/`), la aplicación usa la fuente del sistema. Los tamaños, pesos e interlineados ya están definidos, por lo que al activar la fuente solo cambia el aspecto de las letras. El bloque necesario está preparado y comentado en `Type.kt`.

| Estilo | Tamaño | Peso | Uso |
| --- | --- | --- | --- |
| displaySmall | 36 sp | ExtraBold | Cantidad grande del selector ("500 g") |
| headlineMedium | 26 sp | Bold | Título de pantalla ("Despensa", "Sugerencias") |
| headlineSmall | 22 sp | Bold | Títulos de sección |
| titleLarge | 20 sp | Bold | Nombre de receta, título de diálogo |
| titleMedium | 17 sp | SemiBold | Nombre de alimento |
| titleSmall | 14 sp | SemiBold | Subtítulos |
| bodyLarge | 16 sp | Normal | Texto de lectura |
| bodyMedium | 14 sp | Normal | Texto general |
| bodySmall | 12 sp | Normal | Texto secundario ("Despensa · Pasta") |
| labelLarge | 15 sp | Bold | Botones y chips de filtro |
| labelMedium | 12 sp | SemiBold | Chips de ingredientes, kcal y proteína |
| labelSmall | 10 sp | SemiBold | Etiquetas de categoría y estado |

## Formas

Todo el diseño usa esquinas muy redondeadas, con un contraste claro entre dos familias: tarjetas y paneles redondeados, y elementos interactivos en forma de píldora.

| Forma | Radio | Uso |
| --- | --- | --- |
| extraSmall | 8 dp | Etiquetas pequeñas |
| small | 12 dp | Teclas del teclado numérico |
| medium | 16 dp | Elementos internos de las tarjetas |
| large | 24 dp | Tarjetas de alimentos y recetas |
| extraLarge | 28 dp | Hojas inferiores y diálogos |
| Píldora (`PillShape`) | 50 % | Botones, chips, buscador y pestañas de la barra inferior |

## Componentes

- **Fondo de pantalla:** verde menta, con degradado de blanco a menta en las cabeceras.
- **Tarjetas:** blancas, con esquinas de 24 dp.
- **Botón principal:** verde `#16A34A`, texto blanco, forma de píldora.
- **Botón flotante (+):** verde sólido `#16A34A` con el símbolo en blanco.
- **Barra de navegación:** fondo blanco; la pestaña activa se resalta en verde sólido con letra blanca y las inactivas van en gris azulado `#64748B`.
- **Eliminar:** rojo `#DC2626` en el botón, con icono sobre fondo rojo claro.
- **Chips de filtro:** píldoras; la opción seleccionada va en verde sólido con texto blanco.

## Cómo usarlo en el código

Las pantallas no deben escribir colores, tamaños de letra ni radios a mano. Se accede a todo a través del tema:

- Colores: `MaterialTheme.colorScheme.primary`, `.background`, `.surface`, etc.
- Colores propios de REMY (avisos, información y categorías): `MaterialTheme.remyColors.warning`, `.categoryCereales`, etc.
- Tipografía: `MaterialTheme.typography.titleMedium`, etc.
- Formas: `MaterialTheme.shapes.large`, o `PillShape` para las píldoras.
- Degradado de fondo: `Modifier.background(RemyBackgroundBrush)`.

## Archivos del tema

Todos están en `app/src/main/java/com/remy/app/ui/theme/`:

- `Color.kt`: paleta y degradado de fondo.
- `Type.kt`: estilos tipográficos.
- `Shape.kt`: formas y `PillShape`.
- `Theme.kt`: esquema de colores, colores propios (`RemyColors`) y `REMYTheme`.

## Pendiente

- Añadir los archivos de la fuente (Plus Jakarta Sans, si se confirma) a `res/font/` y activar el bloque comentado de `Type.kt`.
- Mejorar la barra de navegación cuando haya iconos reales (en los bocetos, icono y texto van juntos dentro de la píldora activa).
- Ajustar los chips de filtro y las hojas inferiores cuando existan las pantallas definitivas.
- Anotar el uso de IA en esta tarea en `docs/uso-ia.md`.
