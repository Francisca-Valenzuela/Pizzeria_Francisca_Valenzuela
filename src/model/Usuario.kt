package model

/** Clase base con los datos comunes de cualquier persona del sistema. */
open class Usuario(
        val nombre: String,
        val telefono: String,
        val correo: String
) {
        open fun presentarse(): String =
                "Usuario: $nombre | Tel: $telefono | Correo: $correo"
}