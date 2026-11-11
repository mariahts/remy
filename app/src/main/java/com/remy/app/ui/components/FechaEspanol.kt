package com.remy.app.ui.components

import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Texto largo en español, por ejemplo «8 de noviembre de 2026». */
private val formatoLargo = DateTimeFormatter.ofPattern(
    "d 'de' MMMM 'de' yyyy",
    Locale.forLanguageTag("es")
)

/**
 * Devuelve una marca de tiempo en fecha larga en español.
 * Se queda sin horas: el registro de cocción solo guarda el día en que se
 * cocinó el plato, que es lo que se muestra en el listado y en el detalle.
 */
fun fechaEnEspanol(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatoLargo)

/** Nombre del mes en español, por ejemplo «noviembre». */
fun mesEnEspanol(mes: Month): String =
    mes.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
