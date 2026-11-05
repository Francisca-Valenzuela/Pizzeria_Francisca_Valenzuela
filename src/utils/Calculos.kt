package utils

import model.TotalesPedido

const val CANTIDAD_MINIMA_DESCUENTO = 3
const val PORCENTAJE_DESCUENTO = 0.10
const val PORCENTAJE_IVA = 0.19

/**
 * Calcula el desglose del pedido: descuento por volumen + descuento de promoción
 * se aplican al subtotal y luego se agrega el IVA sobre el monto ya descontado.
 */
fun calcularTotales(subtotal: Double, cantidadPizzas: Int, descuentoPromocion: Double = 0.0): TotalesPedido {
    require(subtotal >= 0) { "El costo del pedido no es válido: $subtotal" }
    require(descuentoPromocion >= 0) { "El descuento de promoción no es válido: $descuentoPromocion" }
    val descuentoVolumen = if (cantidadPizzas > CANTIDAD_MINIMA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO else 0.0
    val promocion = descuentoPromocion.coerceAtMost(subtotal - descuentoVolumen)
    val descuento = descuentoVolumen + promocion
    val base = subtotal - descuento
    val impuestos = base * PORCENTAJE_IVA
    return TotalesPedido(subtotal, descuento, impuestos, base + impuestos, promocion)
}