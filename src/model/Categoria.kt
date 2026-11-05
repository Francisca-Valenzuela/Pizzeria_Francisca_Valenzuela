package model

/** Categorías de producto. Se usan para comparar productos con == (nombre + categoría). */
enum class Categoria(val descripcion: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente"),
    COMBO("Combo")
}