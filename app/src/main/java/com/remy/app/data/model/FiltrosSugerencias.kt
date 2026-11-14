package com.remy.app.data.model

import com.remy.app.data.repository.PreferenciasRepository

/**
 * Filtros de la pantalla de sugerencias (#37).
 *
 * Los valores a cero significan «sin límite» y una región vacía significa
 * «todas». Se guardan en DataStore para recuperarlos al reabrir la app.
 */
data class FiltrosSugerencias(
    val tiposDePlato: Set<String> = emptySet(),
    val maximoFaltantes: Int = PreferenciasRepository.MAXIMO_POR_DEFECTO,
    val tiempoMaximoMinutos: Int = SIN_LIMITE,
    val kcalMaximas: Int = SIN_LIMITE,
    val proteinasMinimas: Int = SIN_LIMITE,
    val region: String = "",
    /** Identificadores de ingredientes que se permiten como faltantes. */
    val faltantesPermitidos: Set<Long> = emptySet(),
    val altaProteina: Boolean = false,
    val bajaHidratos: Boolean = false,
    val bajaGrasa: Boolean = false
) {
    val hayFiltros: Boolean
        get() = tiposDePlato.isNotEmpty() ||
            maximoFaltantes != PreferenciasRepository.MAXIMO_POR_DEFECTO ||
            tiempoMaximoMinutos > SIN_LIMITE ||
            kcalMaximas > SIN_LIMITE ||
            proteinasMinimas > SIN_LIMITE ||
            region.isNotBlank() ||
            faltantesPermitidos.isNotEmpty() ||
            altaProteina ||
            bajaHidratos ||
            bajaGrasa

    companion object {
        const val SIN_LIMITE = 0
        const val UMBRAL_ALTA_PROTEINA = 25
        const val UMBRAL_BAJA_HIDRATOS = 30
        const val UMBRAL_BAJA_GRASA = 15
    }
}
