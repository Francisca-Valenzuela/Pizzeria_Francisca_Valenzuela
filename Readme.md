# Pizzería - Kotlin

Aplicación de consola en Kotlin que simula la operación de una pizzería: registra clientes con validación de datos, controla el inventario de ingredientes, procesa pedidos, asigna repartidores y genera un reporte del día y un resumen detallado de cada pedido.

**Asignatura:** Desarrollo App Móviles (PMY2201) - Duoc UC
**Actividad:** Formativa, Semana 2 - Aplicando utilidades de Kotlin
**Autora:** Francisca Valenzuela

## Funcionalidades

- **Registro de clientes:** valida nombre, dirección, teléfono y correo. Si un dato es inválido, muestra un aviso y usa un valor predeterminado (`(000) 000-0000`, `correo@invalido.com`, "Nombre no informado", "Dirección no informada").
- **Validación de teléfono y correo** con expresiones regulares. El teléfono acepta 9 dígitos que empiezan con 9, con o sin `+56`.
- **Control de inventario:** antes de aceptar un pedido se verifica con un `when` que haya ingredientes suficientes. Si faltan, se rechaza el pedido, se indican los ingredientes insuficientes y los pasos para reabastecer.
- **Cálculo de totales:** subtotal, descuento del 10 % si se piden más de 3 pizzas e IVA del 19 % sobre el monto ya descontado.
- **Preparación de lotes:** calcula los ingredientes necesarios para un lote de pizzas y actualiza el inventario automáticamente.
- **Despacho:** asigna el repartidor disponible más cercano.
- **Reporte del día:** lista todos los pedidos (cliente, pizzas, total y estado) y los agrupa en completados y pendientes.
- **Resumen de cada pedido:** datos del cliente, productos (nombre, precio y cantidad), totales y estado.
- **Notificaciones:** se envían solo a clientes registrados.
- **Manejo de errores:** costo de pedido inválido, texto no numérico y cantidades negativas en el inventario, con mensajes claros.
- - **Reabastecimiento interactivo:** solicita por consola la cantidad a reponer y valida la entrada con `try-catch`, repitiendo la pregunta hasta que el dato sea válido.
- **Ingredientes únicos del menú:** usa un `Set` para listar los ingredientes distintos y verificar que todos estén registrados en el inventario.

## Estructura del proyecto

```
src/
 ├── Main.kt
 ├── model/
 │    ├── EstadoPedido.kt
 │    ├── Usuario.kt
 │    ├── Cliente.kt
 │    ├── Repartidor.kt
 │    ├── Producto.kt
 │    ├── TotalesPedido.kt
 │    └── Pedido.kt
 └── utils/
      ├── Formato.kt
      ├── Validaciones.kt
      ├── Calculos.kt
      ├── Inventario.kt
      ├── Despacho.kt
      └── Reportes.kt
```

| Archivo | Descripción |
|---|---|
| `Main.kt` | Flujo principal: registro de clientes, procesamiento de pedidos, despacho, reporte y resúmenes. |
| `model/EstadoPedido.kt` | Enum con los estados: En preparación, Listo y En camino. |
| `model/Usuario.kt` | Clase base (`open`) con nombre, teléfono y correo. |
| `model/Cliente.kt` | Hereda de `Usuario` y agrega la dirección. |
| `model/Repartidor.kt` | Hereda de `Usuario`; incluye distancia, disponibilidad y `entregarPedido()`. |
| `model/Producto.kt` | Nombre, precio e ingredientes; método `detallarProducto()`. |
| `model/TotalesPedido.kt` | Data class con subtotal, descuento, impuestos y total. |
| `model/Pedido.kt` | Cliente, productos, notas opcionales, estado y repartidor; métodos `calcularTotal()` y `cambiarEstado()`. |
| `utils/Formato.kt` | Función de extensión para mostrar montos en pesos (`$26.000`). |
| `utils/Validaciones.kt` | Validación de texto, teléfono, correo y conversión de precios. |
| `utils/Calculos.kt` | Cálculo de descuento, IVA y total. |
| `utils/Inventario.kt` | Inventario (`Map`), verificación con `when`, preparación de lotes y reabastecimiento. |
| `utils/Despacho.kt` | Asignación del repartidor disponible más cercano con `find`. |
| `utils/Reportes.kt` | Reporte del día, resumen del pedido y notificaciones. |

## Cómo ejecutarlo

1. Clona el repositorio o descarga el proyecto.
2. Ábrelo con IntelliJ IDEA.
3. Abre `src/Main.kt` y ejecuta `main()` con el botón ▶ (Run 'MainKt').
4. El programa corre con datos de ejemplo y muestra el resultado en la consola.

> Si las tildes no se ven bien, agrega `-Dfile.encoding=UTF-8` en las VM options de la configuración de ejecución.

## Ejemplo de salida

Pedido de 4 pizzas (2 Vegetariana a $8.200 y 2 Hawaiana a $8.900):

- Subtotal: $34.200
- Descuento (10 %): -$3.420
- IVA (19 %): $5.848
- Total final: $36.628
- Estado del pedido: Listo

## Conceptos de Kotlin aplicados

- **Flujos de control:** `when` para verificar inventario, `if` como expresión, ciclos `for`.
- **Arreglos y colecciones:** `Array<Pedido>`, `listOf`, `Map` / `MutableMap`, `filter`, `find`, `groupBy`, `sortedBy`, `flatMap`.
- **Strings:** plantillas (`"$variable"` y `"${expresión}"`), expresiones regulares, `padEnd` y `padStart` para dar formato.
- **Funciones:** funciones reutilizables, funciones de extensión y `const val`.
- **POO:** clases, `data class`, `enum class`, herencia (`open`, `override`) y polimorfismo.
- **Manejo de errores:** `try-catch`, `require` y excepciones (`IllegalArgumentException`, `NumberFormatException`).
- **Nulls:** operadores `?.` y `?:`, `let` y `takeIf`.
- - **Arreglos y colecciones:** `Array<Pedido>`, `listOf`, `Set`, `Map` / `MutableMap`, `filter`, `find`, `groupBy`, `sortedBy`, `flatMap`.