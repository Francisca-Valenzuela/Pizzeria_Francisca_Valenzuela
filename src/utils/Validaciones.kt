package utils

const val TELEFONO_POR_DEFECTO = "(000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

/** Rango permitido para la cantidad de productos de un pedido. */
val RANGO_CANTIDAD: IntRange = 1..100

private val REGEX_TELEFONO_CHILENO = Regex("""^(\+?56)?9\d{8}$""")
private val REGEX_TELEFONO_INTERNACIONAL = Regex("""^\+?\d{12}$""")
private val REGEX_CORREO = Regex("""^[\w.+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$""")

fun validarTexto(entrada: String?): String? =
    entrada?.trim()?.takeIf { it.isNotBlank() }

/** Valida el formato nombre@dominio.com. Retorna el correo sin espacios o null si es inválido. */
fun validarCorreo(entrada: String?): String? {
    val texto = entrada?.trim() ?: return null
    return if (REGEX_CORREO.matches(texto)) texto else null
}

/**
 * Procesa un teléfono y lo deja en un formato uniforme con código de país y paréntesis:
 *  - Móvil chileno (9 dígitos, empieza con 9, con o sin +56): +56 (9) XXXX-XXXX
 *  - Número internacional de 12 dígitos:                       +XX (XXX) XXX-XXXX
 * Chile tiene 9 dígitos nacionales (11 con el código de país), por lo que no puede
 * calzar el patrón de 12 dígitos; se conserva la misma estructura ajustando los bloques.
 * Retorna null si no es válido.
 *
 * Ejemplos: "+56 9 8765 4321" -> "+56 (9) 8765-4321"
 *           "987654321"      -> "+56 (9) 8765-4321"
 *           "+34 600 123 4567" -> "+34 (600) 123-4567"
 */
fun formatearTelefono(entrada: String?): String? {
    val limpio = entrada?.filterNot { it.isWhitespace() || it == '-' || it == '(' || it == ')' } ?: return null
    return when {
        REGEX_TELEFONO_CHILENO.matches(limpio) -> {
            val nacional = limpio.takeLast(9)   // 9XXXXXXXX
            "+56 (${nacional[0]}) ${nacional.substring(1, 5)}-${nacional.substring(5)}"
        }
        REGEX_TELEFONO_INTERNACIONAL.matches(limpio) -> {
            val d = limpio.removePrefix("+")
            "+${d.substring(0, 2)} (${d.substring(2, 5)}) ${d.substring(5, 8)}-${d.substring(8)}"
        }
        else -> null
    }
}

/** Retorna la cantidad si es un entero dentro de [RANGO_CANTIDAD]; si no, null. */
fun validarCantidad(entrada: String?): Int? =
    entrada?.trim()?.toIntOrNull()?.takeIf { it in RANGO_CANTIDAD }

fun cantidadEnRango(cantidad: Int): Boolean = cantidad in RANGO_CANTIDAD

/** Convierte un texto a precio. Lanza NumberFormatException o IllegalArgumentException si no es válido. */
fun parsearPrecio(texto: String): Double {
    val precio = texto.trim().toDouble()
    require(precio > 0) { "El costo '$texto' no es válido: debe ser mayor a 0" }
    return precio
}