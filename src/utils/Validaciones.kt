package utils

fun validarTexto(entrada: String?): String? =
    entrada?.trim()?.takeIf { it.isNotBlank() }

fun validarTelefono(entrada: String?): String? {
    val telefono = entrada?.trim()
    return if (telefono != null && telefono.length == 9 && telefono.all { it.isDigit() }) {
        telefono
    } else {
        null
    }
}

fun validarCantidad(entrada: String?): Int? =
    entrada?.trim()?.toIntOrNull()?.takeIf { it > 0 }