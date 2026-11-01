package utils

import java.util.Locale

/** Formatea un monto como pesos chilenos, por ejemplo $26.000 */
fun Double.aPesos(): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,.0f", this)