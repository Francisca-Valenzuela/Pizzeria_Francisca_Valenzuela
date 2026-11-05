package utils

import java.time.LocalDate

/** Fuente de texto del usuario: devuelve null cuando no hay más entrada. */
typealias Lector = () -> String?

/** Lee desde el teclado (consola). */
val lectorConsola: Lector = { readLine() }

/** Simula lo que escribiría el usuario (útil para pruebas). */
fun lectorSimulado(vararg entradas: String): Lector {
    val iterador = entradas.iterator()
    return { if (iterador.hasNext()) iterador.next().also { println(it) } else null }
}

/**
 * Pide un dato hasta que [validar] lo acepte (retorna distinto de null).
 * Si el dato es inválido muestra el error y vuelve a pedirlo. Si no hay más entrada, retorna null.
 */
fun <T : Any> solicitarDato(
    mensaje: String,
    mensajeError: (String) -> String,
    lector: Lector,
    validar: (String) -> T?
): T? {
    while (true) {
        print("   $mensaje")
        val entrada = lector() ?: run {
            println()
            println("   No se recibió ninguna entrada.")
            return null
        }
        val resultado = validar(entrada)
        if (resultado != null) return resultado
        println("   ${mensajeError(entrada)}")
    }
}

/** Pide un correo con formato nombre@dominio.com hasta que sea válido. */
fun solicitarCorreo(lector: Lector, mensaje: String = "Ingrese nuevamente el correo (nombre@dominio.com): "): String? = solicitarDato(
    mensaje = mensaje,
    mensajeError = { "Error: '$it' no es un correo válido. Use el formato nombre@dominio.com." },
    lector = lector,
    validar = ::validarCorreo
)

/** Pide una cantidad dentro de [RANGO_CANTIDAD] hasta que sea válida. */
fun solicitarCantidad(descripcion: String, lector: Lector): Int? = solicitarDato(
    mensaje = "Ingrese la cantidad de $descripcion (${RANGO_CANTIDAD.first}-${RANGO_CANTIDAD.last}): ",
    mensajeError = {
        "Error: '$it' no es válido. Debe ser un entero entre ${RANGO_CANTIDAD.first} y ${RANGO_CANTIDAD.last}."
    },
    lector = lector,
    validar = ::validarCantidad
)

/** Pide un texto no vacío (nombre, dirección, etc.). */
fun solicitarTexto(mensaje: String, lector: Lector): String? = solicitarDato(
    mensaje = mensaje,
    mensajeError = { "Error: este dato no puede estar vacío." },
    lector = lector,
    validar = ::validarTexto
)

/** Pide un teléfono y lo devuelve ya formateado como +XX (XXX) XXX-XXXX. */
fun solicitarTelefono(lector: Lector): String? = solicitarDato(
    mensaje = "Teléfono (ej. +56 9 8765 4321): ",
    mensajeError = { "Error: '$it' no es un teléfono válido. Debe tener 9 dígitos y empezar con 9 (con o sin +56)." },
    lector = lector,
    validar = ::formatearTelefono
)

/** Pide una fecha con formato AAAA-MM-DD. */
fun solicitarFecha(mensaje: String, lector: Lector): LocalDate? = solicitarDato(
    mensaje = mensaje,
    mensajeError = { "Error: '$it' no es una fecha válida. Use el formato AAAA-MM-DD, por ejemplo 2026-11-10." },
    lector = lector,
    validar = { texto -> runCatching { LocalDate.parse(texto.trim()) }.getOrNull() }
)

/** Pide un número de opción entre 1 y [maximo]. */
fun solicitarOpcion(mensaje: String, maximo: Int, lector: Lector): Int? = solicitarDato(
    mensaje = mensaje,
    mensajeError = { "Error: '$it' no es una opción válida. Ingrese un número entre 1 y $maximo." },
    lector = lector,
    validar = { texto -> texto.trim().toIntOrNull()?.takeIf { it in 1..maximo } }
)