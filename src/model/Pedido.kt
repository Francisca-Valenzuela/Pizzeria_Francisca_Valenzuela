package model

import utils.RANGO_CANTIDAD
import utils.calcularDescuentoPromocional
import utils.calcularTotales
import java.time.LocalDate

/**
 * Pedido de un cliente.
 * - `pedidoA + pedidoB` combina dos pedidos del mismo cliente y recalcula el total.
 * - Se puede desestructurar: val (cliente, productos, total) = pedido
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    var repartidor: Repartidor? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    init {
        require(productos.size in RANGO_CANTIDAD) {
            "La cantidad de productos (${productos.size}) debe estar entre ${RANGO_CANTIDAD.first} y ${RANGO_CANTIDAD.last}"
        }
    }

    /** Pizzas y combos (las bebidas no cuentan para el descuento por volumen). */
    val cantidadPizzas: Int get() = productos.count { it.categoria != Categoria.BEBIDA }

    fun calcularTotal(): TotalesPedido = calcularTotales(
        subtotal = productos.sumOf { it.precio },
        cantidadPizzas = cantidadPizzas,
        descuentoPromocion = calcularDescuentoPromocional(productos, fecha)
    )

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    /** Sobrecarga de +: une los productos de ambos pedidos y recalcula el total (con descuentos). */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente " +
                    "(#$id es de ${cliente.nombre} y #${otro.id} es de ${otro.cliente.nombre})"
        }
        val notasUnidas = listOfNotNull(notas, otro.notas)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" | ")
            .ifEmpty { null }
        val fechaFinal = if (fecha.isAfter(otro.fecha)) fecha else otro.fecha
        return Pedido(id, cliente, productos + otro.productos, notasUnidas, fecha = fechaFinal)
    }

    // --- Desestructuración ---
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Double = calcularTotal().total
}