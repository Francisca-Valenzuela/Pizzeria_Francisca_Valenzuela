# Pizzería - Kotlin

Aplicación de consola en Kotlin que simula la operación de una pizzería: registra clientes con validación,
controla inventario, procesa pedidos con descuentos y promociones, asigna repartidores y genera un resumen final.

**Asignatura:** Desarrollo App Móviles (PMY2201) - Duoc UC
**Actividad:** Sumativa, Semana 3 - Creando soluciones con Kotlin
**Autora:** Francisca Valenzuela

## Funcionalidades de la Semana 3

- **Regex:** valida el correo (`nombre@dominio.com`) y, si es inválido, muestra el error y pide reingresarlo.
  Formatea teléfonos con código de país, paréntesis y guion:
    - Móviles chilenos (9 dígitos, empiezan con 9, con o sin `+56`): `+56 (9) XXXX-XXXX`.
    - Números internacionales de 12 dígitos: `+XX (XXX) XXX-XXXX`.

  | Entrada | Resultado |
    |---|---|
  | `987654321` | `+56 (9) 8765-4321` |
  | `+56 9 8765 4321` | `+56 (9) 8765-4321` |
  | `+34 600 123 4567` | `+34 (600) 123-4567` |
  | `12345` | inválido (se vuelve a pedir) |

  > **Decisión de diseño:** el patrón `+XX (XXX) XXX-XXXX` exige 12 dígitos, pero un número chileno tiene
  > 11 con el código de país (56 + 9 dígitos). Por eso se mantiene la misma estructura (código, paréntesis y
  > guion) y se ajustan los bloques según el largo del número.
- **Ranges:** `PERIODO_PROMOCION` (rango de fechas, del 2026-11-01 al 2026-11-30) aplica descuento si el pedido
  cae dentro del periodo; `RANGO_CANTIDAD` (1..100) valida la cantidad de productos y vuelve a pedirla si está
  fuera de rango.
- **Anotación `@Promocionable`:** marca `Pizza` (15 %) y `Combo` (20 %); el procesamiento del pedido la lee con
  reflection para saber qué productos reciben descuento promocional. `Bebida` no la lleva, por lo que no recibe
  descuento.
- **Reflection:** lista propiedades y métodos de `Cliente`, `Pedido`, `Producto`, `Pizza` y `Combo`
  (usa reflection de Java, no requiere la librería `kotlin-reflect`).
- **Sobrecarga de operadores:** `Pedido + Pedido` combina pedidos del mismo cliente y recalcula el total
  (con descuentos); `Producto == Producto` compara por nombre y categoría.
- **Desestructuración:** `val (nombre, correo, telefono) = cliente` y `val (cliente, productos, total) = pedido`.
- **equals/hashCode:** `Cliente` (nombre + correo) y `Producto` (nombre + categoría); `Registro<T>` usa un `Set`
  para evitar duplicados al registrar clientes y productos.
- **Resumen final** en consola con todo lo anterior: pedidos combinados, cliente desestructurado,
  productos promocionables, validación de igualdad, reflection y validaciones.

## Reglas de negocio

- **Menú:** Pepperoni ($8.500), Hawaiana ($8.900), Vegetariana ($8.200), Combo Hawaiana + Bebida ($11.500)
  y Bebida 1.5L ($2.500).
- **Descuento por volumen:** 10 % del subtotal cuando el pedido tiene más de 3 pizzas
  (las bebidas no cuentan).
- **Descuento promocional:** solo si la fecha del pedido está dentro del periodo de promoción y solo para
  productos `@Promocionable` (15 % pizzas, 20 % combos). Se suma al descuento por volumen.
- **IVA:** 19 % sobre el monto ya descontado.
- **Inventario:** cada pizza consume 1 unidad de cada uno de sus ingredientes. Si falta stock, el pedido se
  rechaza, se informa qué falta y se puede reabastecer para reintentarlo.
- **Estados del pedido:** En preparación, Listo y En camino.
- **Despacho:** se asigna el repartidor disponible más cercano; al entregar, el pedido pasa a "En camino" y el
  repartidor queda ocupado.

## Estructura

```
src/
 ├── Main.kt
 ├── model/   Categoria, Promocionable, Producto, TiposProducto (Pizza, Combo, Bebida),
 │            Usuario, Cliente, Repartidor, Pedido, EstadoPedido, TotalesPedido
 └── utils/   Validaciones, Entrada, Promociones, Reflexion, Registro, Calculos,
              Inventario, Despacho, Formato, Reportes, Resumen
```

## Cómo ejecutarlo

1. Abre el proyecto con IntelliJ IDEA.
2. Ejecuta `main()` en `src/Main.kt` (▶ Run 'MainKt').
3. El programa es interactivo: los datos se ingresan por la consola de IntelliJ.

### Flujo de ingreso de datos

1. **Cliente:** nombre, dirección, teléfono (ej. `+56 9 8765 4321`) y correo (`nombre@dominio.com`).
   Si el teléfono o el correo son inválidos, se muestra el error y se piden de nuevo.
2. **Dos pedidos** (el resumen final combina ambos con el operador `+`). Para cada uno:
    - Elegir un producto del menú y su cantidad (entre 1 y 100).
    - Indicar si se agrega otro producto (`s`/`n`).
    - Notas opcionales (Enter para omitir).
    - Fecha del pedido en formato `AAAA-MM-DD`. Si cae dentro del periodo de promoción
      (2026-11-01 a 2026-11-30), se aplica el descuento a los productos `@Promocionable`.
3. Si faltan ingredientes en el inventario, el programa lo informa y permite reabastecer para reintentar el pedido.

Al terminar se muestra el resumen final.

