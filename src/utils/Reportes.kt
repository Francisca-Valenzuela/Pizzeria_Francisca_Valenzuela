package utils

import model.Cliente
import model.Pedido

private val LINEA_DOBLE = "=".repeat(56)
private val LINEA_SIMPLE = "-".repeat(56)

/** Reporte de todos los pedidos del día, agrupados en completados y pendientes. */
fun imprimirReporteDelDia(pedidos: Array<Pedido>) {
    println(LINEA_DOBLE)
    println("            REPORTE DE PEDIDOS DEL DÍA")
    println(LINEA_DOBLE)
    for (pedido in pedidos) {
        val pizzas = pedido.productos.joinToString(", ") { it.nombre }
        val total = pedido.calcularTotal().total.aPesos()
        println("#${pedido.id} | ${pedido.cliente.nombre} | $pizzas | $total | ${pedido.estado.descripcion}")
    }
    println(LINEA_SIMPLE)

    val completados = pedidos.filter { it.estado.estaCompletado }
    val pendientes = pedidos.filter { !it.estado.estaCompletado }
    println("Completados (${completados.size}): ${completados.joinToString { "#${it.id}" }.ifEmpty { "ninguno" }}")
    println("Pendientes  (${pendientes.size}): ${pendientes.joinToString { "#${it.id}" }.ifEmpty { "ninguno" }}")
}

private fun fila(etiqueta: String, valor: String) =
    println(" ${etiqueta.padEnd(26)}: ${valor.padStart(10)}")

fun imprimirResumenPedido(pedido: Pedido) {
    val cliente = pedido.cliente
    val totales = pedido.calcularTotal()
    val notas = pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin notas"
    val repartidor = pedido.repartidor?.nombre ?: "Sin asignar"
    val etiquetaVolumen = "Descuento volumen (${(PORCENTAJE_DESCUENTO * 100).toInt()}%)"
    val etiquetaIva = "IVA (${(PORCENTAJE_IVA * 100).toInt()}%)"
    val estadoPromo = if (estaEnPromocion(pedido.fecha)) "dentro del periodo de promoción" else "fuera de promoción"

    println(LINEA_DOBLE)
    println("              RESUMEN DEL PEDIDO #${pedido.id}")
    println(LINEA_DOBLE)
    println("DATOS DEL CLIENTE")
    println(" Nombre    : ${cliente.nombre}")
    println(" Teléfono  : ${cliente.telefono}")
    println(" Correo    : ${cliente.correo}")
    println(" Dirección : ${cliente.direccion}")
    println(LINEA_SIMPLE)
    println("PRODUCTOS DEL PEDIDO (fecha ${pedido.fecha}: $estadoPromo)")
    pedido.productos.groupBy { it.nombre }.forEach { (nombre, unidades) ->
        val precio = unidades.first().precio
        val subtotalLinea = (precio * unidades.size).aPesos()
        val marca = if (esPromocionable(unidades.first())) " *" else ""
        println(" ${nombre.padEnd(24)} ${precio.aPesos().padStart(8)} x ${unidades.size} = ${subtotalLinea.padStart(9)}$marca")
    }
    println(" (* producto promocionable)")
    println(" Notas: $notas")
    println(LINEA_SIMPLE)
    println("TOTALES")
    fila("Subtotal", totales.subtotal.aPesos())
    fila(etiquetaVolumen, if (totales.descuentoVolumen > 0) "-${totales.descuentoVolumen.aPesos()}" else "No aplica")
    fila("Descuento promoción", if (totales.descuentoPromocion > 0) "-${totales.descuentoPromocion.aPesos()}" else "No aplica")
    fila(etiquetaIva, totales.impuestos.aPesos())
    fila("TOTAL FINAL", totales.total.aPesos())
    println(LINEA_SIMPLE)
    println("ESTADO DEL PEDIDO : ${pedido.estado.descripcion}")
    println("REPARTIDOR        : $repartidor")
    println(LINEA_DOBLE)
}

/** Envía la notificación solo si el cliente está registrado (no es null). */
fun notificarCliente(pedido: Pedido, clientesRegistrados: List<Cliente>) {
    val registrado: Cliente? = clientesRegistrados.find {
        it == pedido.cliente && it.telefono != TELEFONO_POR_DEFECTO
    }
    registrado?.let {
        println("Notificación enviada a ${it.nombre} (${it.correo}): su pedido #${pedido.id} está '${pedido.estado.descripcion}'.")
    } ?: println("Pedido #${pedido.id}: cliente no registrado, no se envía notificación.")
}