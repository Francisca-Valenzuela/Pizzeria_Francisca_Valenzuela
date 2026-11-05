package model

/**
 * Marca las clases de producto (pizzas o combos) que califican para promociones especiales.
 * Se lee en tiempo de ejecución con reflection durante el procesamiento de pedidos.
 *
 * @property descuento porcentaje (0.0 a 1.0) que se descuenta mientras dure el periodo de promoción.
 * @property descripcion texto descriptivo de la promoción.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(val descuento: Double = 0.15, val descripcion: String = "")