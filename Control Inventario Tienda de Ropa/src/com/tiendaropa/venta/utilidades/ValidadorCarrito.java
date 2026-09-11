package com.tiendaropa.venta.utilidades;

import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;

/**
 * Clase de utilidad que valida datos y reglas de negocio del carrito.
 *
 * <p>Centraliza las validaciones relacionadas con el carrito para evitar
 * duplicar código en múltiples lugares.</p>
 */
public final class ValidadorCarrito {

    /**
     * Constructor privado para impedir la instanciación de la utilidad.
     */
    private ValidadorCarrito() {
    }

    /**
     * Indica si el carrito está vacío.
     *
     * @param carrito carrito a validar.
     * @return {@code true} si el carrito está vacío, {@code false} si tiene items.
     * @throws IllegalArgumentException si el carrito es nulo.
     */
    public static boolean validarCarritoVacio(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }
        return carrito.estaVacio();
    }

    /**
     * Indica si una línea de carrito es válida (no nula).
     *
     * @param linea línea de carrito a validar.
     * @return {@code true} si es válida, {@code false} si es nula.
     */
    public static boolean validarLineaCarrito(LineaCarrito linea) {
        return linea != null;
    }

    /**
     * Indica si un código de prenda es válido (no nulo ni vacío).
     *
     * @param codigo código a validar.
     * @return {@code true} si es válido, {@code false} si es nulo o vacío.
     */
    public static boolean validarCodigoPrenda(String codigo) {
        return codigo != null && !codigo.isEmpty();
    }

    /**
     * Indica si el carrito tiene al menos un item. Se usa antes de proceder
     * al checkout.
     *
     * @param carrito carrito a validar.
     * @return {@code true} si tiene items, {@code false} si está vacío.
     * @throws IllegalArgumentException si el carrito es nulo.
     */
    public static boolean validarCarritoTieneItems(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }
        return carrito.obtenerCantidadItems() > 0;
    }

    /**
     * Indica si un subtotal es válido (mayor que cero).
     *
     * @param subtotal subtotal a validar.
     * @return {@code true} si es mayor que 0, {@code false} si es 0 o negativo.
     */
    public static boolean validarSubtotal(double subtotal) {
        return subtotal > 0;
    }
}