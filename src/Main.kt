import model.Cliente
import model.Pedido
import model.Pizza
import utils.calcularDescuento
import utils.calcularSubtotal
import utils.calcularTotal
import utils.hayStock
import utils.validarCantidad
import utils.validarTelefono
import utils.validarTexto

//======================================
// Proyecto: Pizzeria
// Asignatura: Desarrollo App Móviles (PMY2201)
// Semana 1 - Conceptos Base de Kotlin
//======================================

fun main() {

    println("==============================")
    println("   BIENVENIDO A LA PIZZERIA   ")
    println("==============================")

    // --- Datos del cliente ---

    // Nombre
    println("Ingrese su nombre:")
    val nombre: String = validarTexto(readLine()) ?: run {
        println("Aviso: no se ingresó un nombre válido.")
        "Nombre no informado"
    }
    println("Nombre registrado: $nombre")

    // Dirección
    println("")
    println("Ingrese su dirección de entrega:")
    val direccion: String = validarTexto(readLine()) ?: run {
        println("Aviso: no se ingresó una dirección válida.")
        "Dirección no informada"
    }
    println("Dirección registrada: $direccion")

    // Teléfono
    println("")
    println("Ingrese su teléfono (9 dígitos):")
    val telefono: String = validarTelefono(readLine()) ?: run {
        println("Aviso: el teléfono debe tener 9 dígitos numéricos.")
        "Teléfono no informado"
    }
    println("Teléfono registrado: $telefono")

    val cliente = Cliente(nombre, direccion, telefono)

    // --- Datos de la pizza ---
    val pizza = Pizza(
        nombrePizza = "Pepperoni",
        precioUnitario = 1500.0,
        ingredientesDisponibles = 10
    )

    // --- Pedido ---
    println("---------------------------------")
    println("Pizza disponible: ${pizza.nombrePizza} - Precio: $${pizza.precioUnitario}")
    println("Ingredientes disponibles: ${pizza.ingredientesDisponibles}")
    println("Ingrese la cantidad de pizzas:")

    val cantidadPizzas: Int = validarCantidad(readLine()) ?: run {
        println("Aviso: cantidad inválida, se utilizó 1 pizza por defecto.")
        1
    }

    // --- Cálculos ---
    val subtotal: Double = calcularSubtotal(pizza.precioUnitario, cantidadPizzas)
    val descuento: Double = calcularDescuento(subtotal, cantidadPizzas)
    val totalConDescuento: Double = calcularTotal(pizza.precioUnitario, cantidadPizzas)

    println("")
    println("Subtotal: $${subtotal}")
    println(
        if (descuento > 0) "Descuento 10% aplicado: -$${descuento}"
        else "No aplica descuento (se requieren más de 3 pizzas)."
    )

    // --- Verificación de stock ---
    // Se asume que cada pizza consume 1 unidad de ingredientes
    val stockSuficiente: Boolean = hayStock(pizza.ingredientesDisponibles, cantidadPizzas)

    val estadoPedido: String = if (stockSuficiente) {
        pizza.ingredientesDisponibles -= cantidadPizzas
        println("Stock suficiente. Pedido confirmado.")
        println("Ingredientes restantes: ${pizza.ingredientesDisponibles}")
        "Preparación"
    } else {
        println("Stock insuficiente: solo quedan ${pizza.ingredientesDisponibles} ingredientes.")
        println("El pedido no puede ser completado.")
        "Rechazado por falta de stock"
    }

    // --- Creación del pedido ---
    val pedido = Pedido(
        idPedido = 101,
        cantidadPizzas = cantidadPizzas,
        totalPedido = if (stockSuficiente) totalConDescuento else 0.0,
        estadoPedido = estadoPedido,
        repartidorAsignado = if (stockSuficiente) "Mateo Olate" else "Sin asignar"
    )

    // --- Resumen ---
    println("==============================")
    println("       RESUMEN DEL PEDIDO     ")
    println("==============================")
    println("DATOS DEL CLIENTE:")
    println(" - Nombre: ${cliente.nombre}")
    println(" - Dirección: ${cliente.direccion}")
    println(" - Teléfono: ${cliente.telefono}")
    println("------------------------------")
    println("INFORMACIÓN DE LA PIZZA")
    println(" - Pizza: ${pizza.nombrePizza}")
    println(" - Precio unitario: $${pizza.precioUnitario}")
    println("------------------------------")
    println("DETALLES DEL PEDIDO")
    println(" - ID Pedido: ${pedido.idPedido}")
    println(" - Cantidad de pizzas: ${pedido.cantidadPizzas}")
    println(" - Total final a pagar: $${pedido.totalPedido}")
    println(" - Estado del pedido: ${pedido.estadoPedido}")
    println(" - Repartidor asignado: ${pedido.repartidorAsignado}")
    println("==============================")
}