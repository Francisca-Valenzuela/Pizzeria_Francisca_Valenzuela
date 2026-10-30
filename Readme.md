# Pizzería - Kotlin

Aplicación de consola en Kotlin que simula el pedido de una pizzería: registra los datos del cliente, calcula el total con descuento, verifica el stock de ingredientes y genera un resumen del pedido.

**Asignatura:** Desarrollo de Aplicaciones Móviles I (PMY2201) - Duoc UC
**Actividad:** Formativa 1, Semana 1 - Conceptos base de Kotlin
**Autora:** Francisca Valenzuela

## Funcionalidades

- Captura el nombre, la dirección y el teléfono del cliente.
- Valida las entradas. Si un dato es inválido o viene vacío, se muestra un aviso y se usa un mensaje general ("Nombre no informado", "Dirección no informada", "Teléfono no informado").
- El teléfono debe tener exactamente 9 dígitos numéricos.
- Si la cantidad de pizzas es inválida, se usa 1 por defecto.
- Calcula el subtotal y aplica un **10 % de descuento** cuando se piden **más de 3 pizzas**.
- Verifica el stock: cada pizza consume 1 unidad de ingredientes. Si hay stock, confirma el pedido, descuenta el inventario y asigna repartidor. Si no hay, rechaza el pedido con total 0 y sin repartidor.
- Muestra un resumen con los datos del cliente, la información de la pizza y los detalles del pedido.

## Estructura del proyecto

```
src/
 ├── Main.kt
 ├── model/
 │    ├── Cliente.kt
 │    ├── Pizza.kt
 │    └── Pedido.kt
 └── utils/
      ├── Validaciones.kt
      └── Calculos.kt
```

| Archivo | Descripción |
|---|---|
| `Main.kt` | Flujo principal: entrada de datos, pedido, stock y resumen. |
| `model/Cliente.kt` | Datos del cliente (nombre, dirección, teléfono). |
| `model/Pizza.kt` | Datos de la pizza e inventario de ingredientes (`var`, porque se descuenta). |
| `model/Pedido.kt` | Datos del pedido: id, cantidad, total, estado y repartidor. |
| `utils/Validaciones.kt` | Funciones que validan texto, teléfono y cantidad; devuelven `null` si la entrada no es válida. |
| `utils/Calculos.kt` | Subtotal, descuento, total y verificación de stock. |

## Cómo ejecutarlo

1. Clona el repositorio o descarga el proyecto.
2. Ábrelo con IntelliJ IDEA.
3. Abre `src/Main.kt` y ejecuta `main()` con el botón ▶ (Run 'MainKt').
4. Responde en la consola las preguntas que aparecen.

## Ejemplo de uso

Con una cantidad de 5 pizzas (precio unitario $1500):

- Subtotal: $7500.0
- Descuento 10 %: -$750.0
- Total final a pagar: $6750.0
- Estado del pedido: Preparación

## Conceptos de Kotlin aplicados

- Uso de `val` y `var` con tipos explícitos.
- Manejo de nulos con `?.`, `?:` y `isNullOrBlank()`.
- Conversiones seguras con `toIntOrNull()`.
- Expresiones `if` y `run`.
- Funciones reutilizables organizadas en paquetes (`model` y `utils`).
- Constantes con `const val`.