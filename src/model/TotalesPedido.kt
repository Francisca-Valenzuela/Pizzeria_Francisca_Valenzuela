package model

/** Desglose de montos de un pedido. */
data class TotalesPedido(
    val subtotal: Double,
    val descuento: Double,
    val impuestos: Double,
    val total: Double
)