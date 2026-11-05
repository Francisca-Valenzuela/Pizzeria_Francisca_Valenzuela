package utils

import model.Categoria
import model.Cliente
import model.Pedido
import model.Pizza
import model.Producto
import java.time.LocalDate

private val LINEA_DOBLE = "=".repeat(56)
private val LINEA_SIMPLE = "-".repeat(56)

private fun titulo(texto: String) {
    println()
    println(LINEA_SIMPLE)
    println(texto)
    println(LINEA_SIMPLE)
}

private fun siNo(valor: Boolean) = if (valor) "SÍ" else "NO"

fun imprimirEncabezadoResumen() {
    println(LINEA_DOBLE)
    println("   RESUMEN FINAL - CONFIRMACIÓN PARA CLIENTE Y PIZZERÍA")
    println(LINEA_DOBLE)
}

/** 1. Pedidos combinados con el operador + (y rechazo si son de clientes distintos). */
fun resumenPedidosCombinados(a: Pedido, b: Pedido, deOtroCliente: Pedido?) {
    titulo("1. PEDIDOS COMBINADOS (operador +)")
    val combinado = try {
        a + b
    } catch (e: IllegalArgumentException) {
        println(" No se pudo combinar: ${e.message}")
        return
    }
    val ta = a.calcularTotal()
    val tb = b.calcularTotal()
    val tc = combinado.calcularTotal()
    println(" Pedido #${a.id} (${a.cliente.nombre}): ${a.productos.size} producto(s) -> ${ta.total.aPesos()}")
    println(" Pedido #${b.id} (${b.cliente.nombre}): ${b.productos.size} producto(s) -> ${tb.total.aPesos()}")
    println(" Pedido #${a.id} + #${b.id}: ${combinado.productos.size} producto(s)")
    println("   Productos : ${combinado.productos.joinToString { it.nombre }}")
    println("   Notas     : ${combinado.notas ?: "Sin notas"}")
    println("   Subtotal  : ${tc.subtotal.aPesos()}")
    println("   Dcto. volumen   : -${tc.descuentoVolumen.aPesos()}")
    println("   Dcto. promoción : -${tc.descuentoPromocion.aPesos()}")
    println("   IVA (19%) : ${tc.impuestos.aPesos()}")
    println("   TOTAL     : ${tc.total.aPesos()}  (separados sumaban ${(ta.total + tb.total).aPesos()})")
    if (deOtroCliente != null) {
        try {
            a + deOtroCliente
        } catch (e: IllegalArgumentException) {
            println(" Combinación rechazada: ${e.message}")
        }
    }
}

/** 2. Cliente y pedido desestructurados. */
fun resumenDesestructuracion(cliente: Cliente, pedido: Pedido) {
    titulo("2. DESESTRUCTURACIÓN")
    val (nombre, correo, telefono) = cliente
    println(" Cliente  -> nombre: $nombre | correo: $correo | teléfono: $telefono")
    val (clientePedido, productos, total) = pedido
    println(" Pedido #${pedido.id} -> cliente: ${clientePedido.nombre} | productos: ${productos.size} | total: ${total.aPesos()}")
    println("   Productos: ${productos.joinToString { it.nombre }}")
}

/** 3. Productos promocionables detectados con la anotación @Promocionable. */
fun resumenPromocionables(menu: List<Producto>, pedido: Pedido) {
    titulo("3. PRODUCTOS PROMOCIONABLES (@Promocionable)")
    println(" Periodo de promoción: ${PERIODO_PROMOCION.start} a ${PERIODO_PROMOCION.endInclusive}")
    menu.forEach {
        val detalle = if (esPromocionable(it)) "promocionable (${(porcentajePromocion(it) * 100).toInt()}% de descuento)" else "no promocionable"
        println("   - ${it.nombre.padEnd(24)} [${it.categoria.descripcion.padEnd(11)}] $detalle")
    }
    val elegibles = pedido.productos.filter { esPromocionable(it) }.map { it.nombre }.distinct()
    val enPromo = estaEnPromocion(pedido.fecha)
    println(" Pedido #${pedido.id} (fecha ${pedido.fecha}, en promoción: ${siNo(enPromo)})")
    println("   Elegibles: ${elegibles.joinToString().ifEmpty { "ninguno" }}")
    println("   Descuento promocional aplicado: ${pedido.calcularTotal().descuentoPromocion.aPesos()}")
}

/** 4. Validación de igualdad de objetos (equals / hashCode / ==) y duplicados evitados. */
fun resumenIgualdad(
    cliente: Cliente,
    clienteRepetido: Cliente,
    producto: Producto,
    registroClientes: Registro<Cliente>,
    registroProductos: Registro<Producto>
) {
    titulo("4. VALIDACIÓN DE IGUALDAD DE OBJETOS")
    println(" Clientes (clave: nombre + correo)")
    println("   ${cliente.nombre} <${cliente.correo}> == ${clienteRepetido.nombre} <${clienteRepetido.correo}> : ${siNo(cliente == clienteRepetido)}")
    println("   hashCode iguales: ${siNo(cliente.hashCode() == clienteRepetido.hashCode())}")
    val otroProducto = Pizza(producto.nombre, producto.precio + 1000, producto.ingredientes)
    val ingredienteHomonimo = Producto(producto.nombre, 1500.0, emptyList(), Categoria.INGREDIENTE)
    println(" Productos (clave: nombre + categoría)")
    println("   ${producto} == Pizza '${otroProducto.nombre}' con otro precio : ${siNo(producto == otroProducto)}")
    println("   hashCode iguales: ${siNo(producto.hashCode() == otroProducto.hashCode())}")
    println("   ${producto} == ${ingredienteHomonimo} (otra categoría) : ${siNo(producto == ingredienteHomonimo)}")
    println(" Duplicados evitados al registrar")
    println("   Clientes : ${registroClientes.intentos} intentos, ${registroClientes.cantidad} registrados, ${registroClientes.duplicadosRechazados} duplicado(s) rechazado(s)")
    println("   Productos: ${registroProductos.intentos} intentos, ${registroProductos.cantidad} registrados, ${registroProductos.duplicadosRechazados} duplicado(s) rechazado(s)")
}

/** 5. Reflection: estructura de las clases del dominio. */
fun resumenReflection() {
    titulo("5. REFLECTION: ESTRUCTURA DE LAS CLASES")
    imprimirEstructuraClase(model.Cliente::class.java)
    imprimirEstructuraClase(model.Pedido::class.java)
    imprimirEstructuraClase(model.Producto::class.java)
    imprimirEstructuraClase(model.Pizza::class.java)
    imprimirEstructuraClase(model.Combo::class.java)
}

/** 6. Validaciones de datos de entrada (Regex y Ranges). */
fun resumenValidaciones() {
    titulo("6. VALIDACIONES (REGEX Y RANGES)")
    println(" Correo (nombre@dominio.com)")
    listOf("camila.rojas@correo.cl", "diego.soto@", "sin-arroba.cl").forEach {
        println("   ${it.padEnd(24)} -> ${if (validarCorreo(it) != null) "válido" else "inválido"}")
    }
    println(" Teléfono (+56 (9) XXXX-XXXX para Chile; +XX (XXX) XXX-XXXX para 12 dígitos)")
    listOf("987654321", "+56 9 8765 4321", "+34 600 123 4567", "12345").forEach {
        println("   ${it.padEnd(24)} -> ${formatearTelefono(it) ?: "inválido"}")
    }
    println(" Fecha de promoción (${PERIODO_PROMOCION.start} a ${PERIODO_PROMOCION.endInclusive})")
    listOf(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 10, 15), LocalDate.of(2026, 11, 30)).forEach {
        println("   $it -> ${if (estaEnPromocion(it)) "dentro del periodo" else "fuera del periodo"}")
    }
    println(" Cantidad de productos (rango ${RANGO_CANTIDAD.first}-${RANGO_CANTIDAD.last})")
    listOf(0, 1, 50, 100, 101).forEach {
        println("   $it -> ${if (cantidadEnRango(it)) "permitida" else "fuera de rango"}")
    }
    println(LINEA_DOBLE)
}