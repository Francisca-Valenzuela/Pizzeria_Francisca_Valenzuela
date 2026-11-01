package utils

import model.Cliente
import model.Pedido

private val LINEA_DOBLE = "=".repeat(52)
private val LINEA_SIMPLE = "-".repeat(52)

/** Reporte de todos los pedidos del día, agrupados en completados y pendientes. */
fun imprimirReporteDelDia(pedidos: Array<Pedido>) {
    println(LINEA_DOBLE)
    println("          REPORTE DE PEDIDOS DEL DÍA")
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

fun imprimirResumenPedido(pedido: Pedido) {
    val cliente = pedido.cliente
    val totales = pedido.calcularTotal()
    val notas = pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin notas"
    val repartidor = pedido.repartidor?.nombre ?: "Sin asignar"
    val etiquetaDescuento = "Descuento (${(PORCENTAJE_DESCUENTO * 100).toInt()}%)"
    val etiquetaIva = "IVA (${(PORCENTAJE_IVA * 100).toInt()}%)"
    val textoDescuento = if (totales.descuento > 0) "-${totales.descuento.aPesos()}" else "No aplica"

    println(LINEA_DOBLE)
    println("            RESUMEN DEL PEDIDO #${pedido.id}")
    println(LINEA_DOBLE)
    println("DATOS DEL CLIENTE")
    println(" Nombre    : ${cliente.nombre}")
    println(" Teléfono  : ${cliente.telefono}")
    println(" Correo    : ${cliente.correo}")
    println(" Dirección : ${cliente.direccion}")
    println(LINEA_SIMPLE)
    println("PRODUCTOS DEL PEDIDO")
    pedido.productos.groupBy { it.nombre }.forEach { (nombre, unidades) ->
        val precio = unidades.first().precio
        val subtotalLinea = (precio * unidades.size).aPesos()
        println(" ${nombre.padEnd(14)} ${precio.aPesos().padStart(8)} x ${unidades.size} = ${subtotalLinea.padStart(9)}")
    }
    println(" Notas: $notas")
    println(LINEA_SIMPLE)
    println("TOTALES")
    println(" ${"Subtotal".padEnd(20)}: ${totales.subtotal.aPesos().padStart(10)}")
    println(" ${etiquetaDescuento.padEnd(20)}: ${textoDescuento.padStart(10)}")
    println(" ${etiquetaIva.padEnd(20)}: ${totales.impuestos.aPesos().padStart(10)}")
    println(" ${"TOTAL FINAL".padEnd(20)}: ${totales.total.aPesos().padStart(10)}")
    println(LINEA_SIMPLE)
    println("ESTADO DEL PEDIDO : ${pedido.estado.descripcion}")
    println("REPARTIDOR        : $repartidor")
    println(LINEA_DOBLE)
}

/** Envía la notificación solo si el cliente está registrado (no es null). */
fun notificarCliente(pedido: Pedido, clientesRegistrados: List<Cliente>) {
    val registrado: Cliente? = clientesRegistrados.find {
        it.telefono == pedido.cliente.telefono && it.telefono != TELEFONO_POR_DEFECTO
    }
    registrado?.let {
        println("Notificación enviada a ${it.nombre} (${it.correo}): su pedido #${pedido.id} está '${pedido.estado.descripcion}'.")
    } ?: println("Pedido #${pedido.id}: cliente no registrado, no se envía notificación.")
}