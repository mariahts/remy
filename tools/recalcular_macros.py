# -*- coding: utf-8 -*-
"""
Recalcula los macros de cada receta de app/src/main/assets/recetas.json a
partir de sus ingredientes y deja el fichero con el mismo formato.

Sirve para cambiar o añadir recetas sin que los valores de kcal, proteínas,
hidratos y grasas se desincronicen de los ingredientes: la app ya los calcula
al precargar el catálogo (data/PrecargaRecetas.kt), pero el fichero del
catálogo también los guarda, y la prueba instrumentada CatalogoRecetasTest
comprueba que ambos coinciden.

Uso:
    py -X utf8 tools/recalcular_macros.py            # recalcula y escribe
    py -X utf8 tools/recalcular_macros.py --mostrar   # solo muestra el antes/después

Redondeo: half-up (int(math.floor(x + 0.5))), como roundToInt() de Kotlin.
"""
import io
import json
import math
import os
import sys

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RUTA = os.path.join(RAIZ, "app", "src", "main", "assets", "recetas.json")


def redondea(x):
    """Redondeo half-up, el mismo que hace roundToInt() en Kotlin."""
    return int(math.floor(x + 0.5))


def main():
    if not os.path.exists(RUTA):
        print("No se encuentra el catálogo en", RUTA)
        return 2

    with io.open(RUTA, encoding="utf-8") as f:
        datos = json.load(f)

    alimentos = {a["id"]: a for a in datos["alimentos"]}
    cambios = []

    for receta in datos["recetas"]:
        total = {"kcal": 0.0, "proteinas": 0.0, "hidratos": 0.0, "grasas": 0.0}
        for ing in receta["ingredientes"]:
            alimento = alimentos.get(ing["alimentoId"])
            if alimento is None:
                print("AVISO: la receta '%s' usa el alimento %s, que no está en el "
                      "catálogo" % (receta["nombre"], ing["alimentoId"]))
                continue
            factor = ing["cantidad"] / 100.0
            for clave in ("kcal", "proteinas", "hidratos", "grasas"):
                total[clave] += float(alimento.get(clave, 0)) * factor

        antes = (receta["kcal"], receta["proteinas"], receta["hidratos"], receta["grasas"])
        nuevo = (redondea(total["kcal"]), redondea(total["proteinas"]),
                 redondea(total["hidratos"]), redondea(total["grasas"]))
        if antes != nuevo:
            cambios.append((receta["nombre"], antes, nuevo))
        receta["kcal"], receta["proteinas"], receta["hidratos"], receta["grasas"] = nuevo

    if cambios:
        print("%-34s %-20s %-20s" % ("receta", "antes", "después"))
        for nombre, antes, nuevo in cambios:
            print("%-34s %-20s %-20s" % (nombre[:34], antes, nuevo))
    else:
        print("Sin cambios: los macros ya cuadran con los ingredientes")

    if "--mostrar" in sys.argv:
        print("Solo muestra; no se escribe.")
        return 0

    with io.open(RUTA, "w", encoding="utf-8", newline="\n") as f:
        json.dump(datos, f, ensure_ascii=False, indent=4)
        f.write("\n")
    print("Escrito", RUTA)
    return 0


if __name__ == "__main__":
    sys.exit(main())
