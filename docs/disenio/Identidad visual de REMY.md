# Identidad visual de REMY

Tema de Compose de la aplicación · Issue #8 · Actualizado el 12 de noviembre de 2026

## Cómo se construyó

El diseño de la aplicación parte de las pantallas maquetadas en Stitch por Mario
como referencia de partida. Sobre ellas se reorganizó la jerarquía visual y se
volvieron a definir los colores para que la identidad quedara más limpia: se
descartaron la paleta verde y la tipografía Plus Jakarta Sans de la primera
identidad, que ya no se usan en ningún sitio del código, y se adoptó la paleta
crema / terracota / azul tinta con Nunito que se describe aquí.

Todo lo que sigue está implementado en el tema de Compose (`REMYTheme`), que
envuelve la navegación completa desde `MainActivity`, de modo que cualquier
pantalla la hereda sin configuración adicional. Los valores se comprobaron
contra el código: los nombres de esta tabla son los mismos que los de
`ui/theme/Color.kt` y `ui/theme/Type.kt`.

## Decisiones generales

- **Solo modo claro.** El equipo ha decidido no incluir tema oscuro. Las barras
  del sistema se fuerzan a estilo claro en `MainActivity` para que los iconos
  sean siempre legibles.
- **Sin colores dinámicos.** No se usan los colores del fondo de pantalla de
  Android 12+, para que la app se vea igual en todos los dispositivos.
- **Un único punto de definición.** Colores, tipografía y formas viven en
  `ui/theme/` y las pantallas los usan mediante `MaterialTheme`, sin escribir
  valores a mano.

## Colores

### Base

| Nombre | Hexadecimal | Uso |
|---|---|---|
| `FondoCrema` | `#FFFBF5` | Fondo de las pantallas |
| `SuperficieBlanca` | `#FFFFFF` | Tarjetas, barra inferior y superficies principales |
| `BordeTarjeta` | `#F1E9DC` | Bordes y separadores |
| `TextoPrincipal` | `#14213D` | Texto principal |
| `TextoSecundario` | `#475569` | Texto secundario e iconos inactivos |
| `Terracota` | `#C2410C` | Acción principal: botones, chips seleccionados, botón flotante |
| `AzulTinta` | `#14213D` | Estructura y selección: destino activo de la barra, insignias |
| `Melocoton` | `#FFEDD5` | Avatar, círculo del logo y fondos suaves |

El fondo usa un degradado vertical de crema a crema cálida
(`RemyBackgroundBrush`, `#FFFBF5` → `#FFF7ED`).

### Colores de estado

| Nombre | Hexadecimal | Uso |
|---|---|---|
| `AvisoFaltaBg` / `AvisoFaltaFg` | `#FFEDD5` / `#9A3412` | Falta un ingrediente |
| `AvisoProntoBg` / `AvisoProntoFg` | `#FEF3C7` / `#92400E` | Caduca pronto |
| `AvisoCaducadoBg` / `AvisoCaducadoFg` | `#FEE2E2` / `#991B1B` | Caducado |
| `AvisoNeutroBg` / `AvisoNeutroFg` | `#E8EEF3` / `#334155` | Insignia neutra |
| `NaranjaAviso` | `#EA580C` | Texto del aviso al cocinar |

### Tonos pasteles de las miniaturas

Se usan como marcador de posición mientras una receta no tenga foto real, y
como fondo de reserva cuando la carga de la imagen falla.

| Nombre | Hexadecimal |
|---|---|
| `TonoPastelMelocoton` | `#E9A27A` |
| `TonoPastelMostaza` | `#F4C98B` |
| `TonoPastelAzul` | `#9DC3D6` |
| `TonoPastelLavanda` | `#C7D2E8` |
| `TonoPastelRosa` | `#FBCFE8` |
| `TonoPastelAzulClaro` | `#DCE7F5` |

El tono de cada receta se elige de forma estable a partir de su nombre
(`logica/ColorReceta.kt`), de modo que una receta siempre se ve igual.

### Correspondencia con Material 3

| Rol de Material 3 | Valor |
|---|---|
| primary / onPrimary | `#C2410C` / blanco |
| secondary | `#C2410C` |
| tertiary | `#EA580C` |
| background / onBackground | `#FFFBF5` / `#14213D` |
| surface / onSurface | blanco / `#14213D` |
| surfaceVariant / onSurfaceVariant | `#F1E9DC` / `#475569` |
| outline / outlineVariant | `#F1E9DC` / `#F1E9DC` |
| error / errorContainer | `#DC2626` / `#FEE2E2` |
| surfaceContainerHighest | blanco (así las `Card` se ven blancas) |

## Tipografía

La familia es **Nunito**, la misma que usa la maqueta. Se carga con
`compose.ui.text.googlefonts`, por lo que **necesita Google Play Services** en
el dispositivo: sin ellos, Android usa la fuente del sistema y el aspecto
cambia. Está anotado como pendiente empaquetar los archivos de la fuente en
`res/font/` para no depender de ese servicio.

| Estilo | Tamaño | Peso | Uso |
|---|---|---|---|
| displaySmall | 26 sp | Bold | Cantidad grande del selector («500 g») |
| headlineLarge | 24 sp | Bold | Título de detalle de receta |
| headlineMedium | 22 sp | Bold | Título de pantalla |
| headlineSmall | 20 sp | Bold | Título de sección |
| titleLarge | 18 sp | Bold | Nombre de receta, título de diálogo |
| titleMedium | 16 sp | SemiBold | Nombre de alimento |
| titleSmall | 14 sp | SemiBold | Subtítulos |
| bodyLarge | 16 sp | Normal | Texto de lectura |
| bodyMedium | 14 sp | Normal | Texto general |
| bodySmall | 12 sp | Normal | Texto secundario |
| labelLarge | 14 sp | Bold | Botones y chips de filtro |
| labelMedium | 12 sp | SemiBold | Chips de ingredientes, kcal y macros |
| labelSmall | 11 sp | SemiBold | Etiquetas de categoría y estado |

## Formas

Esquinas muy redondeadas, con dos familias: tarjetas y paneles redondeados, y
elementos interactivos en forma de píldora.

| Forma | Radio | Uso |
|---|---|---|
| extraSmall | 8 dp | Etiquetas pequeñas |
| small | 12 dp | Teclas del teclado numérico |
| medium | 16 dp | Elementos internos de las tarjetas |
| large | 24 dp | Tarjetas de alimentos y recetas |
| extraLarge | 24 dp | Tarjetas grandes y diálogos |
| `PillShape` | 50 % | Botones, chips, buscador y destinos activos de la barra |

Las hojas inferiores usan bordes redondeados de 28, 32 y 40 dp
(`SheetShape28`, `SheetShape32`, `SheetShape40`).

## Componentes

- **Fondo de pantalla:** crema, con degradado vertical suave.
- **Tarjetas:** blancas, con esquinas de 24 dp.
- **Botón principal:** terracota `#C2410C`, texto blanco, forma de píldora.
  Deshabilitado mantiene el tono terracota con menos opacidad, para que se
  lea como «no disponible» y no como otro color.
- **Botón flotante (+):** terracota sólido, con el símbolo en blanco.
- **Barra inferior:** fondo blanco y **cuatro destinos** (Inicio, Despensa,
  Recetas y Sugerencias). Cada destino tiene el icono dentro de un círculo de
  40 dp y la etiqueta debajo; el activo se marca con el círculo en azul tinta y
  la etiqueta en texto principal.
- **Insignias de estado:** azul tinta para «Lista para cocinar», melocotón para
  «Faltan N» y los avisos con su fondo propio.
- **Eliminar:** rojo `#DC2626` en el botón, con icono sobre fondo rojo claro.
- **Chips de filtro:** píldoras; la opción seleccionada va en azul tinta con
  texto blanco.

## Cómo usarlo en el código

Las pantallas no deben escribir colores, tamaños de letra ni radios a mano. Se
accede a todo a través del tema:

- Colores: `MaterialTheme.colorScheme.primary`, `.background`, `.surface`, etc.
- Colores propios de REMY: `MaterialTheme.remyColors.warning`,
  `.categoryCereales`, etc.
- Tipografía: `MaterialTheme.typography.titleMedium`, etc.
- Formas: `MaterialTheme.shapes.large`, o `PillShape` para las píldoras.
- Degradado de fondo: `Modifier.background(RemyBackgroundBrush)`.

## Archivos del tema

Todos están en `app/src/main/java/com/remy/app/ui/theme/`:

- `Color.kt`: paleta, tonos pasteles y degradados de fondo.
- `Type.kt`: estilos tipográficos con Nunito.
- `Shape.kt`: formas y `PillShape`.
- `Theme.kt`: esquema de colores, colores propios (`RemyColors`) y `REMYTheme`.

## Pendiente

- Empaquetar los archivos de Nunito en `res/font/` y dejar de depender de Google
  Play Services para la tipografía.
- Modo oscuro: no está en la maqueta y el equipo ha decidido no incluirlo
  todavía; hay que decidirlo antes de la entrega definitiva.
