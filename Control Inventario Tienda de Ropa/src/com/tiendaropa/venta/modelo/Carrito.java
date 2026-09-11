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

        // Agregar la nueva línea
        lineas.add(linea);
        return true;  // Agregada exitosamente
    }

    /**
     * Elimina una línea del carrito por código de prenda.
     * @param codigoPrenda Código único de la prenda a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarLinea(String codigoPrenda) {
        // Usa removeIf para eliminar la línea cuyo código coincida
        return lineas.removeIf(l -> l.getPrenda().getCodigo()
                .equals(codigoPrenda));
    }

    @Override
    public double calcularSubtotal() {
        // Suma todos los subtotales de las líneas usando Stream
        return lineas.stream()
                .mapToDouble(LineaCarrito::getSubtotal)
                .sum();
    }

    @Override
    public int obtenerCantidadItems() {
        // Retorna la cantidad de elementos en la lista
        return lineas.size();
    }

    @Override
    public List<LineaCarrito> getLineas() {
        // Retorna una copia de la lista (no la original)
        return new ArrayList<>(lineas);
    }

    @Override
    public void limpiar() {
        // Vacía la lista y reinicia estado
        lineas.clear();
        estado = EstadoCarrito.ACTIVO;
    }

    @Override
    public boolean estaVacio() {
        // Verifica si la lista está vacía
        return lineas.isEmpty();
    }

    /**
     * Convierte el contenido del carrito en una venta completada.
     *
     * <p>Crea una nueva {@link Venta} con las líneas actuales del carrito,
     * marca el estado como {@link EstadoCarrito#COMPLETADO} y limpia las
     * líneas para dejar el carrito listo para una siguiente compra.</p>
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
        estado = EstadoCarrito.COMPLETADO;
        limpiar();
        return venta;
    }

    /**
     * Devuelve el estado actual del carrito durante su ciclo de vida.
     *
     * @return valor del enumerado {@link EstadoCarrito} que representa el estado.
     */
    public EstadoCarrito getEstado() {
        return estado;
    }


}
