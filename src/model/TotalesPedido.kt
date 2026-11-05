package model

/**
 * Desglose de montos de un pedido.
 * [descuento] es el descuento total; [descuentoPromocion] es la parte que viene de la promoción.
 */
data class TotalesPedido(
    val subtotal: Double,
    val descuento: Double,
    val impuestos: Double,
    val total: Double,
    val descuentoPromocion: Double = 0.0
) {
    val descuentoVolumen: Double get() = descuento - descuentoPromocion
}