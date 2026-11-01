package model

class Repartidor(
    nombre: String,
    telefono: String,
    correo: String,
    val distanciaKm: Double,
    var disponible: Boolean = true
) : Usuario(nombre, telefono, correo) {

    override fun presentarse(): String {
        val estado = if (disponible) "Disponible" else "Ocupado"
        return "Repartidor: $nombre | Tel: $telefono | A $distanciaKm km | $estado"
    }

    /** Pone el pedido "En camino" y deja al repartidor ocupado. */
    fun entregarPedido(pedido: Pedido): String {
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false
        return "$nombre sale a entregar el pedido #${pedido.id} a ${pedido.cliente.direccion}."
    }
}