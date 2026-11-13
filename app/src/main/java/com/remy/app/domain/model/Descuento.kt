package com.remy.app.domain.model

import com.remy.app.data.Alimento

/** Resultado de aplicar el descuento de una receta sobre la despensa. */
data class ResultadoDescuento(
    /** Alimentos de la despensa con la cantidad ya actualizada (listos para persistir). */
    val actualizaciones: List<Alimento>,
    /** Nombres de ingredientes que no se han podido descontar. */
    val noDescontados: List<String>
)
