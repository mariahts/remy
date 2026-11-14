# -*- coding: utf-8 -*-
"""
Recalcula los macros de cada receta de app/src/main/assets/recetas.json a
partir de sus ingredientes (totales de la receta) y deja el fichero con el mismo
formato.

Sirve para cambiar o añadir recetas sin que los valores de kcal, proteínas,
hidratos y grasas se desincronicen de los ingredientes: la app los calcula al
precargar el catálogo (data/local/PrecargaRecetas.kt) y puede usar los valores
del JSON cuando vienen definidos; la prueba instrumentada CatalogoRecetasTest
comprueba la coherencia del catálogo.

Uso:
    py -X utf8 tools/recalcular_macros.py

Ejecutar siempre desde la raíz del proyecto.
"""
import io
import json
import math
import os
import sys

RUTA_JSON = os.path.join("app", "src", "main", "assets", "recetas.json")


def redondea(valor):
    """Redondeo al entero más cercano igual que Kotlin: roundToInt() (half up)."""
    return int(math.floor(float(valor) + 0.5))


def main():
    if not os.path.exists(RUTA_JSON):
        sys.exit("No se encuentra %s. Ejecuta el script desde la raíz del proyecto." % RUTA_JSON)

    with io.open(RUTA_JSON, encoding="utf-8") as f:
        datos = json.load(f)

    macros_alimento = {}
    for alimento in datos["alimentos"]:
        macros_alimento[alimento["id"]] = alimento

    cambios = []
    for receta in datos["recetas"]:
        total = {"kcal": 0.0, "proteinas": 0.0, "hidratos": 0.0, "grasas": 0.0}
        for ing in receta["ingredientes"]:
            alimento = macros_alimento[ing["alimentoId"]]
            factor = ing["cantidad"] / 100.0
            for clave in ("kcal", "proteinas", "hidratos", "grasas"):
                total[clave] += float(alimento.get(clave, 0)) * factor

        antes = (receta["kcal"], receta["proteinas"], receta["hidratos"], receta["grasas"])
        nuevo = (redondea(total["kcal"]), redondea(total["proteinas"]),
                 redondea(total["hidratos"]), redondea(total["grasas"]))
        if antes != nuevo:
            cambios.append((receta["nombre"], antes, nuevo))
        # si el JSON ya trae macros definidos (revisados) los respetamos;
        # solo rellenamos si estaban a 0
        if all(v == 0 for v in antes):
            receta["kcal"], receta["proteinas"], receta["hidratos"], receta["grasas"] = nuevo

    if cambios:
        print("%-34s %-20s %-20s" % ("receta", "en json", "calculado"))
        for nombre, antes, nuevo in cambios:
            marca = " (revisado)" if any(antes) else ""
            print("%-34s %-20s %-20s%s" % (nombre[:34], antes, nuevo, marca))
    else:
        print("Sin cambios: los macros ya coinciden.")

    with io.open(RUTA_JSON, "w", encoding="utf-8", newline="\n") as f:
        json.dump(datos, f, ensure_ascii=False, indent=4)
        f.write("\n")
    print("Escrito", RUTA_JSON)


if __name__ == "__main__":
    main()
