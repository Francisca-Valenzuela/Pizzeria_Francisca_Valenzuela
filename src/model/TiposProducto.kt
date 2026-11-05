package model

/** Pizza del menú. Califica para promociones (15 %). */
@Promocionable(descuento = 0.15, descripcion = "Pizzas del menú")
class Pizza(nombre: String, precio: Double, ingredientes: List<String>) :
    Producto(nombre, precio, ingredientes, Categoria.PIZZA)

/** Combo pizza + bebida. Califica para promociones (20 %). */
@Promocionable(descuento = 0.20, descripcion = "Combo pizza + bebida")
class Combo(nombre: String, precio: Double, ingredientes: List<String>, val bebidaIncluida: String) :
    Producto(nombre, precio, ingredientes, Categoria.COMBO)

/** Bebida. No lleva @Promocionable, por lo que no recibe descuento promocional. */
class Bebida(nombre: String, precio: Double) :
    Producto(nombre, precio, emptyList(), Categoria.BEBIDA)