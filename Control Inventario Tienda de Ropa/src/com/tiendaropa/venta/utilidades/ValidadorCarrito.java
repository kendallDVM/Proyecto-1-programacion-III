package com.tiendaropa.venta.utilidades;



import com.tiendaropa.venta.modelo.Carrito;
import com.tiendaropa.venta.modelo.LineaCarrito;



/**
 * Clase utilidad que valida datos y reglas de negocio del carrito.
 * Centraliza todas las validaciones relacionadas con el carrito
 * para evitar duplicar código en múltiples lugares.
 * @author Liseth Briones
 */
public class ValidadorCarrito {

    /**
     * Valida que el carrito NO esté vacío.
     -------------------------------------
     * @param carrito Carrito a validar
     * @return true si el carrito está vacío, false si tiene items
     * @throws IllegalArgumentException si carrito es nulo
     */
    public static boolean validarCarritoVacio(Carrito carrito) {
        // Verifica que carrito no sea nulo
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }
        // Retorna true si está vacío (estaVacio() retorna true)
        // Retorna false si tiene items
        return carrito.estaVacio();
    }


    /**
     * Valida que una LineaCarrito NO sea nula.
     -----------------------------------------
     * @param linea LineaCarrito a validar
     * @return true si es válida, false si es nula
     */
    public static boolean validarLineaCarrito(LineaCarrito linea) {
        // Retorna true si linea es diferente de null
        // Retorna false si linea es null
        return linea != null;
    }

    /**
     * Valida que un código de prenda NO esté vacío.
     ---------------------------------------------
     * @param codigo Código a validar
     * @return true si es válido, false si es null o vacío
     */
    public static boolean validarCodigoPrenda(String codigo) {
        // Verifica que código no sea null Y no esté vacío
        return codigo != null && !codigo.isEmpty();
    }



    /**
     * Valida que el carrito tenga al menos 1 item.
     * Se usa antes de proceder a checkout.
     * @param carrito Carrito a validar
     * @return true si tiene items (no está vacío)
     * @throws IllegalArgumentException si carrito es nulo
     */
    public static boolean validarCarritoTieneItems(Carrito carrito) {
        // Verifica que carrito no sea nulo
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }
        // Retorna true si tiene items (cantidad > 0)
        // Retorna false si está vacío
        return carrito.obtenerCantidadItems() > 0;
    }

    /**
     * Valida que el subtotal sea válido (mayor a 0).
     * @param subtotal Subtotal a validar
     * @return true si es válido (> 0), false si no
     */
    public static boolean validarSubtotal(double subtotal) {
        // Retorna true si subtotal es mayor que 0
        // Retorna false si es 0 o negativo
        return subtotal > 0;
    }


}
