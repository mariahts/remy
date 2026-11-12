# Cómo añadir o cambiar una receta del catálogo

El catálogo de REMY vive en un solo fichero: `app/src/main/assets/recetas.json`. Ahí están los alimentos y las recetas.

## El prompt

### Para una receta nueva

Copia el bloque de abajo en cualquier asistente y pégale después la tabla de alimentos. Pega después el JSON que te devuelva en el array `recetas` del fichero, respetando el orden de las claves.

```text
Eres chef y redactor de recetas para REMY, una app Android de cocina que calcula
los macros de cada receta a partir de los ingredientes y sugiere platos con lo
que hay en la despensa. Escribe UNA receta para el catálogo.

REGLAS
1. Usa SOLO los alimentos de la tabla que te doy, con su id exacto. No inventes
   alimentos ni cambies los nombres.
2. Unidades: solo "g" o "ml". Los sólidos en "g" y los líquidos en "ml".
3. La cantidad de cada ingrediente es la cantidad TOTAL de la receta, no la de
   una ración.
4. Los macros (kcal, proteínas, hidratos y grasas) se calculan después a partir
   de los ingredientes. Déjalos a 0 en el JSON: no los escribas tú.
5. Pasos: de 4 a 5 pasos concretos y ejecutables. Cada paso dice QUÉ hacer, CÓMO
   (cómo se corta, qué textura, con qué utensilio) y CUÁNDO (minutos,
   temperatura o en qué momento) cuando aplique. Nada de "cocinar hasta que
   esté listo".
6. Cada ingrediente que pongas tiene que aparecer en los pasos.
7. regionOrigen: solo uno de estos valores -> Asiático, Cantábrico, Europeo,
   Mediterráneo, Tropical.
8. tipoPlato: solo Entrante, Principal o Postre.

9. tiempoMinutos es el tiempo total realista, contando la cocción.
10. Porciones: entre 2 y 8. El resultado por ración debe ser una cantidad normal.

FORMATO DE SALIDA
Devuelve SOLO un objeto JSON, sin texto alrededor y sin bloques de código, con
estas claves en este orden exacto:

{"nombre": "...", "kcal": 0, "proteinas": 0, "hidratos": 0, "grasas": 0,
 "regionOrigen": "...", "tipoPlato": "...", "tiempoMinutos": 0,
 "pasos": ["...", "...", "..."],
 "ingredientes": [{"alimentoId": 0, "cantidad": 0, "unidad": "g"}],
 "porciones": 0}

EJEMPLO DE UNA RECETA DE LA MISMA APP
{"nombre": "Ensalada mixta", "kcal": 0, "proteinas": 0, "hidratos": 0,
 "grasas": 0, "regionOrigen": "Mediterráneo", "tipoPlato": "Entrante",
 "tiempoMinutos": 10,
 "pasos": ["Lava la lechuga, el tomate y la cebolla, y secarlos bien con un paño
 de cocina.", "Trocea la lechuga, corta el tomate en rodajas y pica la cebolla
 en juliana fina.", "Coloca todo en un bol grande y aliña con el aceite de oliva
 y la sal.", "Remueve justo antes de servir para que la lechuga no se aguade."],
 "ingredientes": [{"alimentoId": 29, "cantidad": 150, "unidad": "g"},
 {"alimentoId": 3, "cantidad": 200, "unidad": "g"},
 {"alimentoId": 4, "cantidad": 50, "unidad": "g"},
 {"alimentoId": 6, "cantidad": 15, "unidad": "ml"},
 {"alimentoId": 30, "cantidad": 3, "unidad": "g"}],
 "porciones": 2}

TABLA DE ALIMENTOS DISPONIBLES
(id | nombre | kcal | proteínas | hidratos | grasas, todo por 100 g o 100 ml)
```

### Para corregir una receta que ya existe

```text
Eres chef y redactor de recetas para REMY, una app Android de cocina que
calcula los macros de cada receta a partir de los ingredientes.

Te doy una receta del catálogo. Reescríbela para que sea mejor, siguiendo estas
reglas:

REGLAS
1. Usa SOLO los alimentos de la tabla que te doy, con su id exacto. No inventes
   alimentos ni cambies los nombres.
2. Unidades: solo "g" o "ml". Los sólidos en "g" y los líquidos en "ml".
3. La cantidad de cada ingrediente es la cantidad TOTAL de la receta, no la de
   una ración.
4. Los macros (kcal, proteínas, hidratos y grasas) se calculan después a partir
   de los ingredientes. Déjalos a 0 en el JSON: no los escribas tú.
5. Pasos: de 4 a 5 pasos concretos y ejecutables. Cada paso dice QUÉ hacer, CÓMO
   (cómo se corta, qué textura, con qué utensilio) y CUÁNDO (minutos,
   temperatura o en qué momento) cuando aplique. Nada de "cocinar hasta que
   esté listo".
6. Cada ingrediente que pongas tiene que aparecer en los pasos.
7. regionOrigen: solo uno de estos valores -> Asiático, Cantábrico, Europeo,
   Mediterráneo, Tropical.
8. tipoPlato: solo Entrante, Principal o Postre.

9. Si la receta tenía tres pasos vagos, reescríbalos todos con pasos concretos.
10. Revisa el tiempo: que el tiempo total sea realista con los pasos que
    describes.
11. Revisa las cantidades: que la cantidad por ración sea lógica para el plato.
12. Mantén el nombre, el tipoPlato y las porciones salvo que te pidan cambiarlos.

FORMATO DE SALIDA
Devuelve SOLO el objeto JSON de la receta completa, con las mismas claves y en
el mismo orden que la que te doy, y después, en una línea aparte, un comentario
breve con QUÉ has cambiado y POR QUÉ.

TABLA DE ALIMENTOS DISPONIBLES
```

## Los alimentos del catálogo

Macros por 100 g o 100 ml. La app calcula los macros de cada receta sumando la aportación de cada ingrediente.

| id | alimento | kcal | proteínas | hidratos | grasas |
|---|---|---|---|---|---|
| 1 | Arroz | 350 | 7 | 78.0 | 0.6 |
| 2 | Pechuga de pollo | 165 | 31 | 0.0 | 3.6 |
| 3 | Tomate | 18 | 1 | 3.6 | 0.2 |
| 4 | Cebolla | 40 | 1 | 9.0 | 0.1 |
| 5 | Ajo | 149 | 6 | 33.1 | 0.5 |
| 6 | Aceite de oliva | 884 | 0 | 0.0 | 100.0 |
| 7 | Huevo | 143 | 13 | 0.7 | 9.5 |
| 8 | Leche | 61 | 3 | 4.8 | 3.3 |
| 9 | Queso | 402 | 25 | 1.0 | 33.0 |
| 10 | Pan | 265 | 9 | 49.0 | 3.2 |
| 11 | Atún | 132 | 28 | 0.0 | 1.3 |
| 12 | Garbanzos cocidos | 164 | 9 | 27.4 | 2.6 |
| 13 | Lentejas cocidas | 116 | 9 | 20.1 | 0.4 |
| 14 | Zanahoria | 41 | 1 | 9.6 | 0.2 |
| 15 | Pimiento | 31 | 1 | 6.0 | 0.3 |
| 16 | Patata | 77 | 2 | 17.0 | 0.1 |
| 17 | Bacalao | 82 | 18 | 0.0 | 0.7 |
| 18 | Nata | 335 | 2 | 3.0 | 35.0 |
| 19 | Azúcar | 387 | 0 | 100.0 | 0.0 |
| 20 | Harina | 364 | 10 | 76.3 | 1.0 |
| 21 | Mantequilla | 717 | 1 | 0.9 | 81.1 |
| 22 | Plátano | 89 | 1 | 22.8 | 0.3 |
| 23 | Fresa | 32 | 1 | 7.6 | 0.3 |
| 24 | Yogur natural | 59 | 3 | 4.7 | 1.0 |
| 25 | Chocolate negro | 546 | 5 | 46.0 | 31.0 |
| 26 | Champiñón | 22 | 3 | 3.3 | 0.3 |
| 27 | Espinacas | 23 | 3 | 3.6 | 0.4 |
| 28 | Camarón | 99 | 24 | 0.2 | 0.3 |
| 29 | Lechuga | 15 | 1 | 2.9 | 0.2 |
| 30 | Sal | 0 | 0 | 0.0 | 0.0 |
| 31 | Pasta | 350 | 12 | 71.0 | 1.5 |

## Formato de una receta

```json
{
    "nombre": "Ensalada mixta",
    "kcal": 211,
    "proteinas": 4,
    "hidratos": 16,
    "grasas": 16,
    "regionOrigen": "Mediterráneo",
    "tipoPlato": "Entrante",
    "tiempoMinutos": 10,
    "pasos": [
        "Lava la lechuga, el tomate y la cebolla, y secarlos bien con un paño de cocina.",
        "Trocea la lechuga, corta el tomate en rodajas y pica la cebolla en juliana fina.",
        "Coloca todo en un bol grande y aliña con el aceite de oliva y la sal.",
        "Remueve justo antes de servir para que la lechuga no se aguade."
    ],
    "ingredientes": [
        {
            "alimentoId": 29,
            "cantidad": 150,
            "unidad": "g"
        },
        {
            "alimentoId": 3,
            "cantidad": 200,
            "unidad": "g"
        },
        {
            "alimentoId": 4,
            "cantidad": 50,
            "unidad": "g"
        },
        {
            "alimentoId": 6,
            "cantidad": 15,
            "unidad": "ml"
        },
        {
            "alimentoId": 30,
            "cantidad": 3,
            "unidad": "g"
        }
    ],
    "porciones": 2
}
```

## Cómo se aplica un cambio

1. Edita `app/src/main/assets/recetas.json` con la receta nueva o corregida, dejando los macros a 0 (los pone el paso 2).

2. Recalcula los macros: `py -X utf8 tools/recalcular_macros.py` (te dice el antes y el después de cada receta y deja el fichero formateado).

3. Compila y prueba: `./gradlew :app:testDebugUnitTest :app:assembleDebug`.

4. La prueba instrumentada `CatalogoRecetasTest` comprueba que los macros del catálogo coinciden con los ingredientes, que todos los alimentos tienen hidratos y grasas y que todas las recetas tienen pasos e ingredientes válidos: `./gradlew :app:connectedDebugAndroidTest`.

Si cambias el nombre de una receta, la foto del catálogo deja de encontrarse: `ui/components/ImagenesReceta.kt` empareja el nombre exacto de la receta con el fichero `receta_<nombre>.jpg` de `app/src/main/res/drawable-nodpi/`.
