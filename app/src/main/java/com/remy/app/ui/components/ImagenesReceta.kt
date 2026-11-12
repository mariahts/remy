package com.remy.app.ui.components

import androidx.annotation.DrawableRes
import com.remy.app.R

/**
 * Fotos del catalogo de recetas.
 *
 * Cada imagen procede de Wikimedia Commons con licencia libre (CC0, CC BY o
 * CC BY-SA). El credito completo de cada una (autor, licencia y ficha de
 * origen) esta en `docs/fotos-recetas.md`, porque las licencias CC BY y
 * CC BY-SA obligan a dar esos creditos.
 *
 * Se busca por nombre exacto porque el mapa es una lista cerrada del catalogo:
 * si una receta no esta aqui, se sigue mostrando el bloque de color pastel.
 */
object ImagenesReceta {

    @DrawableRes
    private val porNombre: Map<String, Int> = mapOf(
        "Ensalada mixta" to R.drawable.receta_ensalada_mixta,
        "Sopa de verduras" to R.drawable.receta_sopa_de_verduras,
        "Ensalada de atún" to R.drawable.receta_ensalada_de_atun,
        "Crema de zanahoria" to R.drawable.receta_crema_de_zanahoria,
        "Revuelto de champiñones" to R.drawable.receta_revuelto_de_champinones,
        "Camarones al ajillo" to R.drawable.receta_camarones_al_ajillo,
        "Arroz con pollo" to R.drawable.receta_arroz_con_pollo,
        "Tortilla de patatas" to R.drawable.receta_tortilla_de_patatas,
        "Lentejas estofadas" to R.drawable.receta_lentejas_estofadas,
        "Garbanzos con espinacas" to R.drawable.receta_garbanzos_con_espinacas,
        "Pasta con tomate" to R.drawable.receta_pasta_con_tomate,
        "Bacalao al horno" to R.drawable.receta_bacalao_al_horno,
        "Pollo al curry" to R.drawable.receta_pollo_al_curry,
        "Paella de verduras" to R.drawable.receta_paella_de_verduras,
        "Macarrones con queso" to R.drawable.receta_macarrones_con_queso,
        "Flan de huevo" to R.drawable.receta_flan_de_huevo,
        "Arroz con leche" to R.drawable.receta_arroz_con_leche,
        "Yogur con fresas" to R.drawable.receta_yogur_con_fresas,
        "Plátano con chocolate" to R.drawable.receta_platano_con_chocolate,
        "Bizcocho de plátano" to R.drawable.receta_bizcocho_de_platano
    )

    /** Nombre de las recetas que tienen foto en el catalogo. */
    val recetasConFoto: Set<String> = porNombre.keys

    /** Identificador del recurso de la foto de la receta, o null si no tiene. */
    @DrawableRes
    fun para(nombreReceta: String): Int? = porNombre[nombreReceta.trim()]
}
