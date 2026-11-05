import model.Bebida
import model.Categoria
import model.Cliente
import model.Combo
import model.EstadoPedido
import model.Pedido
import model.Pizza
import model.Producto
import model.Repartidor
import model.Usuario
import utils.Lector
import utils.PERIODO_PROMOCION
import utils.Registro
import utils.TELEFONO_POR_DEFECTO
import utils.asignarRepartidor
import utils.calcularIngredientesNecesarios
import utils.calcularTotales
import utils.cantidadEnRango
import utils.crearInventario
import utils.esPromocionable
import utils.estaEnPromocion
import utils.formatearTelefono
import utils.imprimirEncabezadoResumen
import utils.imprimirReporteDelDia
import utils.imprimirResumenPedido
import utils.ingredientesSinRegistro
import utils.lectorConsola
import utils.mostrarFaltantes
import utils.notificarCliente
import utils.obtenerIngredientesUnicos
import utils.parsearPrecio
import utils.prepararLote
import utils.reabastecerIngrediente
import utils.resumenDesestructuracion
import utils.resumenIgualdad
import utils.resumenPedidosCombinados
import utils.resumenPromocionables
import utils.resumenReflection
import utils.resumenValidaciones
import utils.solicitarCantidad
import utils.solicitarCorreo
import utils.solicitarFecha
import utils.solicitarOpcion
import utils.solicitarTelefono
import utils.solicitarTexto
import utils.verificarDisponibilidad
import java.time.LocalDate

//======================================
// Proyecto: Pizzería
// Asignatura: Desarrollo App Móviles (PMY2201)
// Semana 3 - Creando soluciones con Kotlin
//======================================

/**
 * Solicita por consola los datos de un cliente. Nombre, dirección, teléfono y correo se validan
 * (Regex) y, si son inválidos, se muestra el error y se vuelven a pedir.
 * Retorna null si se acaba la entrada antes de completar los datos.
 */
fun solicitarCliente(lector: Lector): Cliente? {
    val nombre = solicitarTexto("Nombre: ", lector) ?: return null
    val direccion = solicitarTexto("Dirección: ", lector) ?: return null
    val telefono = solicitarTelefono(lector) ?: return null
    val correo = solicitarCorreo(lector, "Correo (nombre@dominio.com): ") ?: return null
    return Cliente(nombre, telefono, correo, direccion)
}

/** Acepta el pedido solo si la cantidad está en rango y hay ingredientes suficientes; si no, lo rechaza (null). */
fun procesarPedido(
    id: Int,
    cliente: Cliente,
    productos: List<Producto>,
    notas: String?,
    inventario: MutableMap<String, Int>,
    fecha: LocalDate = LocalDate.now()
): Pedido? {
    println("Procesando pedido #$id de ${cliente.nombre} (${productos.size} producto(s), fecha $fecha)...")
    if (!cantidadEnRango(productos.size)) {
        println("   Pedido #$id RECHAZADO: la cantidad de productos (${productos.size}) está fuera del rango permitido (1-100).")
        return null
    }
    val promocionables = productos.filter { esPromocionable(it) }.map { it.nombre }.distinct()
    if (estaEnPromocion(fecha)) {
        println("   Fecha dentro del periodo de promociones. Promocionables (@Promocionable): ${promocionables.joinToString().ifEmpty { "ninguno" }}.")
    } else {
        println("   Fecha fuera del periodo de promociones: no se aplica descuento promocional.")
    }
    val faltantes = prepararLote(productos.flatMap { it.ingredientes }, 1, inventario)
    return if (faltantes.isEmpty()) {
        println("   Pedido #$id ACEPTADO. Inventario actualizado.")
        Pedido(id, cliente, productos, notas, fecha = fecha)
    } else {
        println("   Pedido #$id RECHAZADO.")
        mostrarFaltantes(faltantes)
        null
    }
}

/** Pide por consola la cantidad a reabastecer y repite hasta que el dato sea válido. */
fun solicitarCantidadReabastecimiento(inventario: MutableMap<String, Int>, ingrediente: String, lector: Lector) {
    while (true) {
        print("   Ingrese la cantidad de '$ingrediente' a reabastecer: ")
        val entrada = lector() ?: run {
            println("\n   No se recibió ninguna entrada. Se omite el reabastecimiento.")
            return
        }
        try {
            val cantidad = entrada.trim().toInt()
            reabastecerIngrediente(inventario, ingrediente, cantidad)
            println("   Stock de $ingrediente actualizado: ${inventario[ingrediente]} unidad(es).")
            return
        } catch (e: NumberFormatException) {
            println("   Error: '$entrada' no es un número entero. Ingrese solo dígitos, por ejemplo 5.")
        } catch (e: IllegalArgumentException) {
            println("   Error: ${e.message}. Intente nuevamente.")
        }
    }
}

/**
 * Toma un pedido por consola: producto, cantidad (rango 1-100), notas y fecha.
 * Si faltan ingredientes, ofrece reabastecer y reintenta una vez.
 */
fun tomarPedido(
    id: Int,
    cliente: Cliente,
    ofertas: List<Producto>,
    inventario: MutableMap<String, Int>,
    lector: Lector
): Pedido? {
    println("\n--- PEDIDO #$id DE ${cliente.nombre.uppercase()} ---")
    ofertas.forEachIndexed { i, p -> println("   ${i + 1}. ${p.detallarProducto()}") }

    val productos = mutableListOf<Producto>()
    do {
        val opcion = solicitarOpcion("Elija un producto (1-${ofertas.size}): ", ofertas.size, lector) ?: break
        val elegido = ofertas[opcion - 1]
        val cantidad = solicitarCantidad("'${elegido.nombre}'", lector) ?: break
        repeat(cantidad) { productos.add(elegido) }
        print("   ¿Agregar otro producto? (s/n): ")
    } while (lector()?.trim()?.lowercase() == "s")

    if (productos.isEmpty()) {
        println("   No se agregaron productos. Pedido cancelado.")
        return null
    }
    print("   Notas del pedido (Enter para omitir): ")
    val notas = lector()?.trim()?.takeIf { it.isNotEmpty() }
    val fecha = solicitarFecha(
        "Fecha del pedido AAAA-MM-DD (promoción: ${PERIODO_PROMOCION.start} a ${PERIODO_PROMOCION.endInclusive}): ",
        lector
    ) ?: LocalDate.now()

    var pedido = procesarPedido(id, cliente, productos, notas, inventario, fecha)
    if (pedido == null) {
        val faltantes = verificarDisponibilidad(inventario, calcularIngredientesNecesarios(productos.flatMap { it.ingredientes }, 1))
        if (faltantes.isNotEmpty()) {
            println("\nFaltan ingredientes para el pedido #$id. Debe reabastecer: ${faltantes.keys.joinToString(", ")}.")
            faltantes.keys.forEach { solicitarCantidadReabastecimiento(inventario, it, lector) }
            println("   Reintentando el pedido #$id.")
            pedido = procesarPedido(id, cliente, productos, notas, inventario, fecha)
        }
    }
    return pedido
}

fun demostrarManejoDeErrores(inventario: MutableMap<String, Int>) {
    println("\n--- MANEJO DE ERRORES ---")
    try {
        Pizza("Pizza Gourmet", -9500.0, listOf("Masa"))
    } catch (e: IllegalArgumentException) {
        println("Error de datos: ${e.message}. Corrija el precio e intente nuevamente.")
    }
    try {
        parsearPrecio("ocho mil")
    } catch (e: NumberFormatException) {
        println("Error: el costo 'ocho mil' no es un número. Ingrese solo dígitos, por ejemplo 8500.")
    } catch (e: IllegalArgumentException) {
        println("Error de datos: ${e.message}.")
    }
    try {
        calcularTotales(-1000.0, 2)
    } catch (e: IllegalArgumentException) {
        println("Error de cálculo: ${e.message}. Revise el costo del pedido.")
    }
    try {
        reabastecerIngrediente(inventario, "Queso mozzarella", -5)
    } catch (e: IllegalArgumentException) {
        println("Error de inventario: ${e.message}. Ingrese una cantidad mayor o igual a 0.")
    }
}

fun main() {
    val lector: Lector = lectorConsola

    println("==================================================")
    println("              BIENVENIDO A LA PIZZERÍA")
    println("==================================================")

    // --- Datos base: inventario (Map), repartidores (listOf) y menú ---
    val inventario: MutableMap<String, Int> = crearInventario()

    val repartidores: List<Repartidor> = listOf(
        Repartidor("Sofía Araya", "955551111", "sofia@pizzeria.cl", 0.8, disponible = false),
        Repartidor("Mateo Olate", "955552222", "mateo@pizzeria.cl", 1.2),
        Repartidor("Ignacio Vera", "955553333", "ignacio@pizzeria.cl", 2.5)
    )

    val pepperoni = Pizza("Pepperoni", 8500.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Pepperoni"))
    val hawaiana = Pizza("Hawaiana", 8900.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Jamón", "Piña"))
    val vegetariana = Pizza("Vegetariana", 8200.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Champiñones", "Aceitunas"))
    val comboHawaiana = Combo("Combo Hawaiana + Bebida", 11500.0, hawaiana.ingredientes, "Bebida 1.5L")
    val bebida = Bebida("Bebida 1.5L", 2500.0)

    // --- Registro de productos sin duplicados (equals/hashCode por nombre + categoría) ---
    println("\n--- REGISTRO DE PRODUCTOS (sin duplicados) ---")
    val registroProductos = Registro<Producto>("Producto")
    listOf<Producto>(pepperoni, hawaiana, vegetariana, comboHawaiana, bebida).forEach {
        registroProductos.registrar(it, it.nombre)
    }
    // Mismo nombre y categoría que otra pizza (aunque cambie el precio): duplicado
    registroProductos.registrar(Pizza("Pepperoni", 9000.0, pepperoni.ingredientes), "Pepperoni (precio distinto)")
    // Mismo nombre pero otra categoría: es otro producto
    registroProductos.registrar(Producto("Pepperoni", 1500.0, emptyList(), Categoria.INGREDIENTE), "Pepperoni (ingrediente adicional)")

    val menu: List<Producto> = registroProductos.todos()
    val ofertas: List<Producto> = menu.filter { it.categoria != Categoria.INGREDIENTE }

    // --- Set: ingredientes únicos del menú y validación contra el inventario ---
    val ingredientesMenu: Set<String> = obtenerIngredientesUnicos(menu)
    println("\nIngredientes distintos del menú (${ingredientesMenu.size}): ${ingredientesMenu.joinToString(", ")}")
    val sinRegistro: Set<String> = ingredientesSinRegistro(menu, inventario)
    if (sinRegistro.isEmpty()) {
        println("Todos los ingredientes del menú están registrados en el inventario.")
    } else {
        println("Ingredientes sin registro en el inventario: ${sinRegistro.joinToString(", ")}")
    }

    demostrarManejoDeErrores(inventario)

    // --- Registro del cliente: datos solicitados por consola (Regex) ---
    println("\n--- REGISTRO DE CLIENTE (ingrese sus datos) ---")
    val registroClientes = Registro<Cliente>("Cliente")
    val cliente = solicitarCliente(lector) ?: run {
        println("\nNo se recibieron los datos del cliente. Fin del programa.")
        return
    }
    registroClientes.registrar(cliente, cliente.nombre)

    // Mismo nombre y correo (en mayúsculas): duplicado
    val clienteRepetido = Cliente(cliente.nombre.uppercase(), cliente.telefono, cliente.correo.uppercase(), cliente.direccion)
    registroClientes.registrar(clienteRepetido, "${clienteRepetido.nombre} (mismo nombre y correo)")

    // Segundo cliente fijo, solo para mostrar que no se pueden combinar pedidos de clientes distintos
    val otroCliente = Cliente("Diego Soto", formatearTelefono("911112222") ?: TELEFONO_POR_DEFECTO, "diego.soto@correo.cl", "Los Leones 450")
    registroClientes.registrar(otroCliente, otroCliente.nombre)
    val clientesRegistrados: List<Cliente> = registroClientes.todos()

    // --- Dos pedidos del cliente (cantidad en rango 1-100, fecha en periodo de promoción) ---
    val pedido1 = tomarPedido(101, cliente, ofertas, inventario, lector)
    val pedido2 = tomarPedido(102, cliente, ofertas, inventario, lector)
    val pedidoAjeno = Pedido(900, otroCliente, listOf(vegetariana), null, fecha = LocalDate.of(2026, 10, 15))

    // --- Arreglo con los pedidos del día ---
    val pedidosDelDia: Array<Pedido> = listOfNotNull(pedido1, pedido2).toTypedArray()
    if (pedidosDelDia.isEmpty()) {
        println("\nNo hay pedidos aceptados. Fin del programa.")
        return
    }

    // --- Cambios de estado y despacho ---
    println("\n--- DESPACHO ---")
    pedidosDelDia.first().let { pedido ->
        pedido.cambiarEstado(EstadoPedido.LISTO)
        val repartidor = asignarRepartidor(repartidores)
        if (repartidor != null) {
            pedido.repartidor = repartidor
            println(repartidor.entregarPedido(pedido))
        } else {
            println("No hay repartidores disponibles para el pedido #${pedido.id}.")
        }
    }

    // --- Reporte del día y resumen de cada pedido ---
    println()
    imprimirReporteDelDia(pedidosDelDia)
    println()
    for (pedido in pedidosDelDia) {
        imprimirResumenPedido(pedido)
        notificarCliente(pedido, clientesRegistrados)
        println()
    }

    // --- Polimorfismo: Usuario -> Cliente / Repartidor ---
    println("--- USUARIOS DEL SISTEMA ---")
    val usuarios: List<Usuario> = clientesRegistrados + repartidores
    usuarios.forEach { println(it.presentarse()) }

    // --- Inventario final ---
    println("\n--- INVENTARIO FINAL ---")
    inventario.forEach { (ingrediente, cantidad) -> println(" ${ingrediente.padEnd(18)}: $cantidad") }

    // --- Resumen final (Paso 6): integra todas las funcionalidades ---
    println()
    imprimirEncabezadoResumen()
    if (pedido1 != null && pedido2 != null) {
        resumenPedidosCombinados(pedido1, pedido2, pedidoAjeno)
    } else {
        println("\nNo se combinaron pedidos: se necesitan 2 pedidos aceptados.")
    }
    val pedidoParaDetalle = pedido2 ?: pedido1!!
    resumenDesestructuracion(cliente, pedidoParaDetalle)
    resumenPromocionables(menu, pedidoParaDetalle)
    resumenIgualdad(cliente, clienteRepetido, pepperoni, registroClientes, registroProductos)
    resumenReflection()
    resumenValidaciones()
}