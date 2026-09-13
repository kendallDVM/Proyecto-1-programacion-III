package com.tiendaropa.venta.modelo;


import com.tiendaropa.venta.servicio.ICarrito;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el carrito de compras del cliente.
 * Gestiona la colección de prendas (LineaCarrito) que el cliente desea comprar.
 *
 * Recordar que cada prenda es única (ropa segunda mano), no hay duplicados.
 * @author Liseth Briones
 */



public class Carrito implements ICarrito {

                        //Atributos

    private ArrayList<LineaCarrito> lineas;  // Lista de prendas en el carrito
    private EstadoCarrito estado;            // Estado actual del carrito

                        //Constructor

    /**
     * Crea un nuevo carrito vacío en estado ACTIVO.
     */
    public Carrito() {
        this.lineas = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
    }


                // MÉTODOS DE AGREGAR/ELIMINAR
    /**
     * Agrega una línea (prenda) al carrito.
     * NO permite duplicados: si la prenda ya existe, rechaza.
     *
     * @param linea LineaCarrito a agregar
     * @return true si se agregó exitosamente, false si ya estaba (duplicado)
     * @throws IllegalArgumentException si linea es nula
     */
    @Override
    public boolean agregarLinea(LineaCarrito linea) {
        // Validación: linea no puede ser nula
        if (linea == null) {
            throw new IllegalArgumentException("Linea de Carrito no puede ser nula");
        }

        // Verificar que NO haya otra línea con el MISMO código de prenda
        boolean yaExiste = lineas.stream()
                .anyMatch(l -> l.getPrenda().getCodigo()
                        .equals(linea.getPrenda().getCodigo()));

        if (yaExiste) {
            return false;  // Prenda ya está en carrito, rechazar
        }

        // Si venimos de una compra completada, el carrito vuelve a estar activo
        if (estado == EstadoCarrito.COMPLETADO) {
            estado = EstadoCarrito.ACTIVO;
        }

        // Agregar la nueva línea
        lineas.add(linea);
        return true;  // Agregada exitosamente
    }

    /**
     * Elimina una línea del carrito por código de prenda.
     *
     * @param codigoPrenda código único de la prenda a eliminar.
     * @return {@code true} si se eliminó, {@code false} si no existía.
     */
    @Override
    public boolean eliminarLinea(String codigoPrenda) {
        // Usa removeIf para eliminar la línea cuyo código coincida
        return lineas.removeIf(l -> l.getPrenda().getCodigo()
                .equals(codigoPrenda));
    }

    /**
     * Calcula el subtotal del carrito sumando el subtotal de todas sus líneas.
     *
     * @return subtotal del carrito en colones.
     */
    @Override
    public double calcularSubtotal() {
        return lineas.stream()
                .mapToDouble(LineaCarrito::getSubtotal)
                .sum();
    }

    /**
     * Devuelve las líneas del carrito.
     *
     * @return copia de la lista de líneas (no la referencia original).
     */
    @Override
    public List<LineaCarrito> getLineas() {
        return new ArrayList<>(lineas);
    }

    /**
     * Vacía completamente el carrito (cancela la compra en curso) y
     * reinicia su estado a {@code ACTIVO}.
     */
    @Override
    public void limpiar() {
        lineas.clear();
        estado = EstadoCarrito.ACTIVO;
    }

    /**
     * Indica si el carrito está vacío.
     *
     * @return {@code true} si no tiene items, {@code false} en caso contrario.
     */
    @Override
    public boolean estaVacio() {
        return lineas.isEmpty();
    }

    /**
     * Convierte el contenido del carrito en una venta completada.
     *
     * <p>Crea una nueva {@link Venta} con las líneas actuales del carrito,
     * vacía las líneas y deja el carrito en estado
     * {@link EstadoCarrito#COMPLETADO}. Al agregar una nueva línea, el
     * carrito vuelve automáticamente a estado {@link EstadoCarrito#ACTIVO},
     * quedando listo para una siguiente compra.</p>
     *
     * @param codigoFactura código único de factura asignado a la venta.
     * @return la venta generada a partir del contenido del carrito.
     * @throws IllegalStateException si el carrito está vacío.
     */
    @Override
    public Venta checkout(String codigoFactura) {
        if (estaVacio()) {
            throw new IllegalStateException("No se puede completar la venta: el carrito está vacío.");
        }
        estado = EstadoCarrito.PROCESANDO;
        Venta venta = new Venta(codigoFactura, lineas);
        lineas.clear();
        estado = EstadoCarrito.COMPLETADO;
        return venta;
    }

}
