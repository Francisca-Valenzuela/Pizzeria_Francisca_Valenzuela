package model

import utils.calcularTotales

class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    var repartidor: Repartidor? = null
) {
    fun calcularTotal(): TotalesPedido =
        calcularTotales(productos.sumOf { it.precio }, productos.size)

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }
}