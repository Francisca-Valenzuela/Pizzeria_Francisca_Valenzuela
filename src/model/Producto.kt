package model

import utils.aPesos

/**
 * Producto de la pizzería. Es `open` para que Pizza, Combo y Bebida lo extiendan.
 * Dos productos son iguales (==) si tienen el mismo nombre y la misma categoría.
 */
open class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>,
    val categoria: Categoria = Categoria.PIZZA
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío" }
        require(precio > 0) { "El precio de '$nombre' no es válido ($precio): debe ser mayor a 0" }
    }

    fun detallarProducto(): String =
        "$nombre [${categoria.descripcion}] - ${precio.aPesos()} - " +
                "Ingredientes: ${ingredientes.joinToString(", ").ifEmpty { "ninguno" }}"

    /** Sobrecarga del operador ==: mismo nombre (sin distinguir mayúsculas) y misma categoría. */
    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) && categoria == other.categoria
    }

    override fun hashCode(): Int = 31 * nombre.trim().lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.descripcion})"
}