package utils

const val CANTIDAD_MINIMA_DESCUENTO = 3
const val PORCENTAJE_DESCUENTO = 0.10

fun calcularSubtotal(precio: Double, cantidad: Int): Double =
    precio * cantidad

fun calcularDescuento(subtotal: Double, cantidad: Int): Double =
    if (cantidad > CANTIDAD_MINIMA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO else 0.0

fun calcularTotal(precio: Double, cantidad: Int): Double {
    val subtotal = calcularSubtotal(precio, cantidad)
    return subtotal - calcularDescuento(subtotal, cantidad)
}

fun hayStock(disponibles: Int, solicitadas: Int): Boolean =
    disponibles >= solicitadas