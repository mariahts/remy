# Fotos de las recetas: qué imagen va en cada una

Las 20 recetas del catálogo necesitan una foto. Las que hay ahora
(`res/drawable-nodpi/receta_<slug>.jpg`) sirven de relleno mientras tanto; su
crédito está en [`fotos-recetas.md`](fotos-recetas.md) y se sustituye cuando
lleguen las definitivas.

## Cómo se llama el fichero

`app/src/main/res/drawable-nodpi/receta_<slug>.jpg`, con el slug de la tabla
siguiente. Al reemplazar un fichero, se conserva el nombre: la app lo busca
por el nombre exacto de la receta.

## Formato que deben cumplir

| | |
|---|---|
| Formato | JPEG (o WebP), **sin canal alfa** |
| Proporción | Cuadrada 1:1 |
| Resolución | 1080 × 1080 px (mínimo 800 × 800) |
| Peso | Menos de 250 KB por foto |
| Encuadre | El plato centrado, fondo limpio, luz natural, sin texto ni logotipos |

La miniatura se recorta con `ContentScale.Crop`, así que una imagen con el
sujeto descentrado pierde parte del plato. Las fotos actuales son de 720 px de
ancho; se pueden reescalar a 1080 × 1080 sin pérdida apreciable.

## Las 20 recetas

| # | Receta | Fichero | Raciones | Tiempo | Qué imagen buscar |
|---|---|---|---|---|---|
| 1 | Ensalada mixta | `receta_ensalada_mixta.jpg` | 2 | 10 min | Ensalada de hojas verdes con tomate y cebolla, aliñada |
| 2 | Sopa de verduras | `receta_sopa_de_verduras.jpg` | 4 | 35 min | Plato de crema de verduras, con algo de color en la superficie |
| 3 | Ensalada de atún | `receta_ensalada_de_atun.jpg` | 2 | 10 min | Ensalada con atún y tomate en un bol |
| 4 | Crema de zanahoria | `receta_crema_de_zanahoria.jpg` | 4 | 30 min | Crema naranja de zanahoria en cuenco |
| 5 | Revuelto de champiñones | `receta_revuelto_de_champinones.jpg` | 2 | 15 min | Revuelto de huevo con champiñones en la sartén o en el plato |
| 6 | Camarones al ajillo | `receta_camarones_al_ajillo.jpg` | 2 | 15 min | Platos de gambas al ajillo con ajo dorado |
| 7 | Arroz con pollo | `receta_arroz_con_pollo.jpg` | 4 | 45 min | Plato de arroz con pollo troceado |
| 8 | Tortilla de patatas | `receta_tortilla_de_patatas.jpg` | 4 | 40 min | Tortilla de patatas cuajada, vista desde arriba o de canto |
| 9 | Lentejas estofadas | `receta_lentejas_estofadas.jpg` | 4 | 50 min | Plato de lentejas estofadas con verduras |
| 10 | Garbanzos con espinacas | `receta_garbanzos_con_espinacas.jpg` | 4 | 35 min | Plato de garbanzos con espinacas |
| 11 | Pasta con tomate | `receta_pasta_con_tomate.jpg` | 4 | 25 min | Plato de pasta con salsa de tomate, sin albóndigas |
| 12 | Bacalao al horno | `receta_bacalao_al_horno.jpg` | 4 | 30 min | Lomo o rodaja de bacalao horneado con patatas |
| 13 | Pollo al curry | `receta_pollo_al_curry.jpg` | 4 | 35 min | Plato de pollo con curry y arroz |
| 14 | Paella de verduras | `receta_paella_de_verduras.jpg` | 4 | 45 min | Paella en la paellera, con verduras visibles |
| 15 | Macarrones con queso | `receta_macarrones_con_queso.jpg` | 4 | 30 min | Macarrones con bechamel y queso gratinado |
| 16 | Flan de huevo | `receta_flan_de_huevo.jpg` | 4 | 50 min | Flan de huevo recién desmoldado, con caramelo |
| 17 | Arroz con leche | `receta_arroz_con_leche.jpg` | 4 | 40 min | Bol de arroz con leche, espolvoreado con canela |
| 18 | Yogur con fresas | `receta_yogur_con_fresas.jpg` | 2 | 5 min | Bol de yogur con fresas frescas cortadas |
| 19 | Plátano con chocolate | `receta_platano_con_chocolate.jpg` | 2 | 5 min | Plátano partido con chocolate fundido |
| 20 | Bizcocho de plátano | `receta_bizcocho_de_platano.jpg` | 8 | 55 min | Rebanada de bizcocho de plátano |

## Al replacearlas

1. Guardar cada imagen como `receta_<slug>.jpg` en
   `app/src/main/res/drawable-nodpi/`.
2. Actualizar `docs/fotos-recetas.md` con el crédito de la nueva imagen
   (autor, licencia y enlace), que es lo que exigen las licencias CC BY y
   CC BY-SA.
3. `./gradlew :app:testDebugUnitTest :app:assembleDebug` y comprobar el
   listado y el detalle de las recetas afectadas en el emulador.
