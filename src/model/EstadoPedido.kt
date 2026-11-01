package model

/**
 * Estados posibles de un pedido.
 * [estaCompletado] indica si la preparación ya terminó (Listo o En camino).
 */
enum class EstadoPedido(val descripcion: String, val estaCompletado: Boolean) {
    EN_PREPARACION("En preparación", false),
    LISTO("Listo", true),
    EN_CAMINO("En camino", true)
}