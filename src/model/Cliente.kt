package model

/**
 * Cliente de la pizzería. Dos clientes son iguales si coinciden su nombre y su correo.
 * Se puede desestructurar: val (nombre, correo, telefono) = cliente
 */
class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    override fun presentarse(): String =
        "Cliente: $nombre | Tel: $telefono | Correo: $correo | Dirección: $direccion"

    // --- Desestructuración ---
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // --- Igualdad por atributos clave (nombre y correo) ---
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
                correo.trim().equals(other.correo.trim(), ignoreCase = true)
    }

    override fun hashCode(): Int =
        31 * nombre.trim().lowercase().hashCode() + correo.trim().lowercase().hashCode()

    override fun toString(): String = "$nombre <$correo>"
}