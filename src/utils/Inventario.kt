package utils
import model.Producto

/** Cada pizza consume 1 unidad de cada uno de sus ingredientes. */
const val UNIDADES_POR_INGREDIENTE = 1

fun crearInventario(): MutableMap<String, Int> = mutableMapOf(
    "Masa" to 20,
    "Salsa de tomate" to 20,
    "Queso mozzarella" to 20,
    "Pepperoni" to 3,
    "Jamón" to 8,
    "Piña" to 8,
    "Champiñones" to 2,
    "Aceitunas" to 2
)

/** Cantidad total de cada ingrediente para [cantidadPizzas] pizzas con la lista dada. */
fun calcularIngredientesNecesarios(ingredientes: List<String>, cantidadPizzas: Int): Map<String, Int> =
    ingredientes.groupingBy { it }.eachCount()
        .mapValues { (_, veces) -> veces * UNIDADES_POR_INGREDIENTE * cantidadPizzas }

/** Devuelve ingrediente -> unidades que faltan. Si el mapa está vacío, hay stock completo. */
fun verificarDisponibilidad(inventario: Map<String, Int>, necesarios: Map<String, Int>): Map<String, Int> {
    val faltantes = mutableMapOf<String, Int>()
    for ((ingrediente, requerido) in necesarios) {
        val disponible = inventario[ingrediente]
        when {
            disponible == null -> faltantes[ingrediente] = requerido
            disponible < requerido -> faltantes[ingrediente] = requerido - disponible
            else -> Unit
        }
    }
    return faltantes
}

/**
 * Calcula lo necesario para un lote de pizzas y, si alcanza, descuenta del inventario.
 * Retorna los faltantes (vacío = lote preparado y inventario actualizado).
 */
fun prepararLote(
    ingredientes: List<String>,
    cantidadPizzas: Int,
    inventario: MutableMap<String, Int>
): Map<String, Int> {
    val necesarios = calcularIngredientesNecesarios(ingredientes, cantidadPizzas)
    val faltantes = verificarDisponibilidad(inventario, necesarios)
    if (faltantes.isEmpty()) {
        necesarios.forEach { (ingrediente, requerido) ->
            inventario[ingrediente] = (inventario[ingrediente] ?: 0) - requerido
        }
    }
    return faltantes
}

/** Suma stock al inventario. Lanza IllegalArgumentException si la cantidad es negativa. */
fun reabastecerIngrediente(inventario: MutableMap<String, Int>, ingrediente: String, cantidad: Int) {
    require(cantidad >= 0) { "La cantidad de '$ingrediente' no puede ser negativa ($cantidad)" }
    inventario[ingrediente] = (inventario[ingrediente] ?: 0) + cantidad
}

fun mostrarFaltantes(faltantes: Map<String, Int>) {
    println("   Ingredientes insuficientes:")
    faltantes.forEach { (ingrediente, cantidad) ->
        println("    - $ingrediente: faltan $cantidad unidad(es)")
    }
    println("   Pasos para reabastecer:")
    println("    1. Revisar el stock real en bodega.")
    println("    2. Hacer el pedido de ${faltantes.keys.joinToString(", ")} al proveedor.")
    println("    3. Registrar la recepción en el inventario.")
    println("    4. Volver a ingresar el pedido del cliente.")
}

/** Ingredientes distintos que usa el menú. Un Set no admite elementos repetidos. */
fun obtenerIngredientesUnicos(menu: List<Producto>): Set<String> =
    menu.flatMap { it.ingredientes }.toSet()

/** Ingredientes del menú que no están registrados en el inventario. */
fun ingredientesSinRegistro(menu: List<Producto>, inventario: Map<String, Int>): Set<String> =
    obtenerIngredientesUnicos(menu) - inventario.keys