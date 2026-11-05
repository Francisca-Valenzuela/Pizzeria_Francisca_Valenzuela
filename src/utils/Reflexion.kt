package utils

import model.Promocionable
import java.lang.reflect.Modifier

private fun jerarquia(clase: Class<*>): List<Class<*>> =
    generateSequence<Class<*>>(clase) { it.superclass }.takeWhile { it != Any::class.java }.toList()

/** Propiedades (campos) de la clase y de sus superclases, vía reflection. */
fun listarPropiedades(clase: Class<*>): List<String> =
    jerarquia(clase).flatMap { it.declaredFields.toList() }
        .filter { !it.isSynthetic && !Modifier.isStatic(it.modifiers) }
        .map { "${it.name}: ${it.type.simpleName}" }
        .sorted()

/** Métodos de la clase y de sus superclases, vía reflection. */
fun listarMetodos(clase: Class<*>): List<String> =
    jerarquia(clase).flatMap { it.declaredMethods.toList() }
        .filter { !it.isSynthetic && !it.name.contains('$') }
        .map { m -> "${m.name}(${m.parameterTypes.joinToString { p -> p.simpleName }}): ${m.returnType.simpleName}" }
        .distinct()
        .sorted()

/** Imprime la estructura de una clase (y si está marcada con @Promocionable). */
fun imprimirEstructuraClase(clase: Class<*>) {
    val anotacion = clase.getAnnotation(Promocionable::class.java)
    val marca = if (anotacion != null) " [@Promocionable ${(anotacion.descuento * 100).toInt()}%]" else ""
    println(" Clase ${clase.simpleName}$marca")
    println("   Propiedades:")
    listarPropiedades(clase).forEach { println("     - $it") }
    println("   Métodos:")
    listarMetodos(clase).forEach { println("     - $it") }
}