package utils

const val TELEFONO_POR_DEFECTO = "(000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

private val REGEX_TELEFONO = Regex("""^(\+56)?9\d{8}$""")
private val REGEX_CORREO = Regex("""^[\w.+-]+@[\w-]+(\.[\w-]+)+$""")

fun validarTexto(entrada: String?): String? =
    entrada?.trim()?.takeIf { it.isNotBlank() }

/** Acepta 9 dígitos que empiezan con 9, con o sin +56, espacios o guiones. */
fun validarTelefono(entrada: String?): String? {
    val texto = entrada?.trim() ?: return null
    val limpio = texto.filterNot { it.isWhitespace() || it == '-' }
    return if (REGEX_TELEFONO.matches(limpio)) texto else null
}

fun validarCorreo(entrada: String?): String? {
    val texto = entrada?.trim() ?: return null
    return if (REGEX_CORREO.matches(texto)) texto else null
}

/** Convierte un texto a precio. Lanza NumberFormatException o IllegalArgumentException si no es válido. */
fun parsearPrecio(texto: String): Double {
    val precio = texto.trim().toDouble()
    require(precio > 0) { "El costo '$texto' no es válido: debe ser mayor a 0" }
    return precio
}