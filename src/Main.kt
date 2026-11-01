import model.Cliente
import model.EstadoPedido
import model.Pedido
import model.Producto
import model.Repartidor
import model.Usuario
import utils.CORREO_POR_DEFECTO
import utils.TELEFONO_POR_DEFECTO
import utils.asignarRepartidor
import utils.calcularTotales
import utils.crearInventario
import utils.imprimirReporteDelDia
import utils.imprimirResumenPedido
import utils.mostrarFaltantes
import utils.notificarCliente
import utils.parsearPrecio
import utils.prepararLote
import utils.reabastecerIngrediente
import utils.validarCorreo
import utils.validarTelefono
import utils.validarTexto
import utils.obtenerIngredientesUnicos
import utils.ingredientesSinRegistro

//======================================
// Proyecto: Pizzería
// Asignatura: Desarrollo App Móviles (PMY2201)
// Semana 2 - Aplicando utilidades de Kotlin
//======================================

/** Registra un cliente validando sus datos; si alguno es inválido usa un valor predeterminado. */
fun registrarCliente(nombre: String?, direccion: String?, telefono: String?, correo: String?): Cliente {
    val nombreFinal = validarTexto(nombre) ?: run {
        println("   Aviso: nombre inválido, se usa 'Nombre no informado'.")
        "Nombre no informado"
    }
    val direccionFinal = validarTexto(direccion) ?: run {
        println("   Aviso: dirección inválida, se usa 'Dirección no informada'.")
        "Dirección no informada"
    }
    val telefonoFinal = validarTelefono(telefono) ?: run {
        println("   Aviso: teléfono '$telefono' inválido, se usa $TELEFONO_POR_DEFECTO.")
        TELEFONO_POR_DEFECTO
    }
    val correoFinal = validarCorreo(correo) ?: run {
        println("   Aviso: correo '$correo' inválido, se usa $CORREO_POR_DEFECTO.")
        CORREO_POR_DEFECTO
    }
    return Cliente(nombreFinal, telefonoFinal, correoFinal, direccionFinal)
}

/** Acepta el pedido solo si hay ingredientes suficientes; si no, lo rechaza (null). */
fun procesarPedido(
    id: Int,
    cliente: Cliente,
    productos: List<Producto>,
    notas: String?,
    inventario: MutableMap<String, Int>
): Pedido? {
    println("Procesando pedido #$id de ${cliente.nombre} (${productos.size} pizza(s))...")
    val faltantes = prepararLote(productos.flatMap { it.ingredientes }, 1, inventario)
    return if (faltantes.isEmpty()) {
        println("   Pedido #$id ACEPTADO. Inventario actualizado.")
        Pedido(id, cliente, productos, notas)
    } else {
        println("   Pedido #$id RECHAZADO.")
        mostrarFaltantes(faltantes)
        null
    }
}

/** Pide por consola la cantidad a reabastecer y repite hasta que el dato sea válido. */
fun solicitarCantidadReabastecimiento(inventario: MutableMap<String, Int>, ingrediente: String) {
    while (true) {
        print("   Ingrese la cantidad de '$ingrediente' a reabastecer: ")
        val entrada = readLine() ?: run {
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

fun demostrarManejoDeErrores(inventario: MutableMap<String, Int>) {
    println("\n--- MANEJO DE ERRORES ---")
    try {
        Producto("Pizza Gourmet", -9500.0, listOf("Masa"))
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

    val pepperoni = Producto("Pepperoni", 8500.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Pepperoni"))
    val hawaiana = Producto("Hawaiana", 8900.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Jamón", "Piña"))
    val vegetariana = Producto("Vegetariana", 8200.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Champiñones", "Aceitunas"))
    val menu: List<Producto> = listOf(pepperoni, hawaiana, vegetariana)

    println("\n--- MENÚ ---")
    menu.forEach { println(" ${it.detallarProducto()}") }

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

    // --- Registro de clientes (validación con strings) ---
    println("\n--- REGISTRO DE CLIENTES ---")
    val camila = registrarCliente("Camila Rojas", "Av. Providencia 1234", "+56 9 8765 4321", "camila.rojas@correo.cl")
    val diego = registrarCliente("Diego Soto", "Los Leones 450", "12345", "diego.soto@")
    val valentina = registrarCliente("Valentina Pérez", "Irarrázaval 2020", "987654321", "vale.perez@correo.cl")
    val clientesRegistrados: List<Cliente> = listOf(camila, valentina)

    // --- Procesamiento de pedidos (when + inventario) ---
    println("\n--- PROCESAMIENTO DE PEDIDOS ---")
    val pedido101 = procesarPedido(101, camila, listOf(pepperoni, pepperoni, hawaiana), "Sin cebolla, tocar timbre", inventario)
    val pedido102 = procesarPedido(102, diego, listOf(vegetariana, vegetariana, hawaiana, hawaiana), null, inventario)
    var pedido103 = procesarPedido(103, valentina, listOf(pepperoni, pepperoni, pepperoni), "   ", inventario)

    if (pedido103 == null) {
        println("\nFaltan ingredientes para el pedido #103. Debe reabastecer Pepperoni.")
        solicitarCantidadReabastecimiento(inventario, "Pepperoni")
        println("   Reintentando el pedido #103.")
        pedido103 = procesarPedido(103, valentina, listOf(pepperoni, pepperoni, pepperoni), "   ", inventario)
    }

    // --- Arreglo con los pedidos del día ---
    val pedidosDelDia: Array<Pedido> = listOfNotNull(pedido101, pedido102, pedido103).toTypedArray()

    // --- Cambios de estado y despacho ---
    println("\n--- DESPACHO ---")
    pedidosDelDia.firstOrNull { it.id == 101 }?.let { pedido ->
        pedido.cambiarEstado(EstadoPedido.LISTO)
        val repartidor = asignarRepartidor(repartidores)
        if (repartidor != null) {
            pedido.repartidor = repartidor
            println(repartidor.entregarPedido(pedido))
        } else {
            println("No hay repartidores disponibles para el pedido #${pedido.id}.")
        }
    }
    pedidosDelDia.firstOrNull { it.id == 102 }?.cambiarEstado(EstadoPedido.LISTO)

    // --- Reporte del día ---
    println()
    imprimirReporteDelDia(pedidosDelDia)

    // --- Resumen de cada pedido ---
    println()
    for (pedido in pedidosDelDia) {
        imprimirResumenPedido(pedido)
        notificarCliente(pedido, clientesRegistrados)
        println()
    }

    // --- Polimorfismo: Usuario -> Cliente / Repartidor ---
    println("--- USUARIOS DEL SISTEMA ---")
    val usuarios: List<Usuario> = clientesRegistrados + diego + repartidores
    usuarios.forEach { println(it.presentarse()) }

    // --- Inventario final ---
    println("\n--- INVENTARIO FINAL ---")
    inventario.forEach { (ingrediente, cantidad) -> println(" ${ingrediente.padEnd(18)}: $cantidad") }
}