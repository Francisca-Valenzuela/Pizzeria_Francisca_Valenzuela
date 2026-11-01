package utils

import model.TotalesPedido

const val CANTIDAD_MINIMA_DESCUENTO = 3
const val PORCENTAJE_DESCUENTO = 0.10
const val PORCENTAJE_IVA = 0.19

/**
 * Calcula el desglose del pedido: el descuento se aplica al subtotal
 * y luego se agrega el IVA sobre el monto ya descontado.
 */
fun calcularTotales(subtotal: Double, cantidadPizzas: Int): TotalesPedido {
    require(subtotal >= 0) { "El costo del pedido no es válido: $subtotal" }
    val descuento = if (cantidadPizzas > CANTIDAD_MINIMA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO else 0.0
    val base = subtotal - descuento
    val impuestos = base * PORCENTAJE_IVA
    return TotalesPedido(subtotal, descuento, impuestos, base + impuestos)
}