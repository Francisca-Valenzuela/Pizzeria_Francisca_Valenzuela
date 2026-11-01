package model

class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    override fun presentarse(): String =
        "Cliente: $nombre | Tel: $telefono | Correo: $correo | Dirección: $direccion"
}
