package utils

import model.Producto
import model.Promocionable
import java.time.LocalDate

/** Periodo de promociones (rango de fechas, ambos extremos incluidos). */
val PERIODO_PROMOCION = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)

/** Indica si la fecha cae dentro del periodo de promociones. */
fun estaEnPromocion(fecha: LocalDate): Boolean = fecha in PERIODO_PROMOCION

/** Detecta con reflection si la clase del producto está marcada con @Promocionable. */
fun esPromocionable(producto: Producto): Boolean =
    producto.javaClass.isAnnotationPresent(Promocionable::class.java)

/** Porcentaje de descuento declarado en la anotación (0.0 si el producto no es promocionable). */
fun porcentajePromocion(producto: Producto): Double =
    producto.javaClass.getAnnotation(Promocionable::class.java)?.descuento ?: 0.0

/** Descuento total de promoción: solo aplica a productos promocionables y si la fecha está en el rango. */
fun calcularDescuentoPromocional(productos: List<Producto>, fecha: LocalDate): Double =
    if (!estaEnPromocion(fecha)) 0.0 else productos.sumOf { porcentajePromocion(it) * it.precio }