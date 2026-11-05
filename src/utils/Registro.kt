package utils

/**
 * Registro sin duplicados. Usa un Set, por lo que depende de equals() y hashCode()
 * de la entidad (Cliente: nombre + correo; Producto: nombre + categoría).
 */
class Registro<T : Any>(private val tipo: String) {
    private val elementos = LinkedHashSet<T>()

    var intentos: Int = 0
        private set
    val cantidad: Int get() = elementos.size
    val duplicadosRechazados: Int get() = intentos - cantidad

    /** Retorna true si se registró; false si ya existía un elemento igual (duplicado). */
    fun registrar(elemento: T, etiqueta: String): Boolean {
        intentos++
        val nuevo = elementos.add(elemento)
        if (nuevo) println("   $tipo '$etiqueta' registrado.")
        else println("   Aviso: $tipo '$etiqueta' ya existe, se omite el duplicado.")
        return nuevo
    }

    fun todos(): List<T> = elementos.toList()
}