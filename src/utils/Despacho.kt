package utils

import model.Repartidor

/** Asigna el repartidor disponible más cercano (o null si no hay ninguno). */
fun asignarRepartidor(repartidores: List<Repartidor>): Repartidor? =
    repartidores.sortedBy { it.distanciaKm }.find { it.disponible }