package com.remy.app.ui.components

import androidx.annotation.DrawableRes
import com.remy.app.R

/**
 * Fotos del catalogo de recetas.
 *
 * La mayoria proceden de Openverse, que agrega repositorios de imagenes con
 * licencia libre (CC0, CC BY o CC BY-SA), y una es una foto del propio equipo.
 * El credito completo de cada una (autor, licencia y ficha de origen) esta en
 * `docs/fotos-recetas.md`, porque las licencias CC BY y CC BY-SA obligan a dar
 * esos creditos.
 *
 * Se busca por nombre exacto porque el mapa es una lista cerrada del catalogo:
 * si una receta no esta aqui, se sigue mostrando el bloque de color pastel.
 */
object ImagenesReceta {

    @DrawableRes
    private val porNombre: Map<String, Int> = mapOf(
        "Gazpacho andaluz" to R.drawable.receta_gazpacho_andaluz,
        "Ensalada de atún y huevo" to R.drawable.receta_ensalada_de_atun_y_huevo,
        "Croquetas de pollo" to R.drawable.receta_croquetas_de_pollo,
        "Camarones al ajillo" to R.drawable.receta_camarones_al_ajillo,
        "Ensalada de tomate y cebolla" to R.drawable.receta_ensalada_de_tomate_y_cebolla,
        "Tortilla de patatas con cebolla" to R.drawable.receta_tortilla_de_patatas_con_cebolla,
        "Garbanzos con espinacas" to R.drawable.receta_garbanzos_con_espinacas,
        "Lentejas con verduras" to R.drawable.receta_lentejas_con_verduras,
        "Arroz con pollo y pimiento" to R.drawable.receta_arroz_con_pollo_y_pimiento,
        "Pasta con tomate y atún" to R.drawable.receta_pasta_con_tomate_y_atun,
        "Bacalao al pil-pil" to R.drawable.receta_bacalao_al_pil_pil,
        "Bacalao a lo pobre" to R.drawable.receta_bacalao_a_lo_pobre,
        "Marmitako de atún" to R.drawable.receta_marmitako_de_atun,
        "Wok de pollo con verduras" to R.drawable.receta_wok_de_pollo_con_verduras,
        "Arroz frito con huevo y camarones" to R.drawable.receta_arroz_frito_con_huevo_y_camarones,
        "Pasta con champiñones y nata" to R.drawable.receta_pasta_con_champinones_y_nata,
        "Arroz con leche" to R.drawable.receta_arroz_con_leche,
        "Tarta de queso" to R.drawable.receta_tarta_de_queso,
        "Plátano con chocolate negro" to R.drawable.receta_platano_con_chocolate_negro,
        "Fresas con nata" to R.drawable.receta_fresas_con_nata,
        "Albóndigas en salsa" to R.drawable.receta_albondigas_en_salsa,
        "Avena cremosa con plátano y canela" to R.drawable.receta_avena_cremosa_con_platano_y_canela,
        "Huevos revueltos cremosos" to R.drawable.receta_huevos_revueltos,
        "Tortitas americanas" to R.drawable.receta_tortitas_americanas,
        "Tostadas francesas" to R.drawable.receta_tostadas_francesas,
        "Wrap de pollo" to R.drawable.receta_wrap_de_pollo
    )

    /** Nombre de las recetas que tienen foto en el catalogo. */
    val recetasConFoto: Set<String> = porNombre.keys

    /** Identificador del recurso de la foto de la receta, o null si no tiene. */
    @DrawableRes
    fun para(nombreReceta: String): Int? = porNombre[nombreReceta.trim()]
}
