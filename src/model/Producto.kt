package model

import utils.aPesos

class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío" }
        require(precio > 0) { "El precio de '$nombre' no es válido ($precio): debe ser mayor a 0" }
    }

    fun detallarProducto(): String =
        "$nombre - ${precio.aPesos()} - Ingredientes: ${ingredientes.joinToString(", ")}"
}