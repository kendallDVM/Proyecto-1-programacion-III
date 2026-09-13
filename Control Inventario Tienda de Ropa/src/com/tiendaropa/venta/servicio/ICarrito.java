package com.tiendaropa.venta.servicio;


import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.modelo.Venta;
import java.util.List;


/**
 * Interfaz que define el contrato para las operaciones que un carrito debe soportar.
 *
 * VENTAJA: La GUI no conoce detalles de implementación (Carrito).
 * Si cambiamos cómo funciona internamente, GUI sigue funcionando.
 *
 * @author Liseth Briones
 */





public interface ICarrito {


    /**
     * Agrega una línea (prenda) al carrito.
     * @param linea LineaCarrito a agregar
     * @return true si se agregó, false si ya existía (duplicado)
     * @throws IllegalArgumentException si linea es nula
     */
    boolean agregarLinea(LineaCarrito linea);

    /**
     * Elimina una línea del carrito por código de prenda.
     * @param codigoPrenda Código único de la prenda
     * @return true si se eliminó, false si no existía
     */
    boolean eliminarLinea(String codigoPrenda);

    /**
     * Calcula el subtotal del carrito (suma de precios).
     * @return double: subtotal en colones
     */
    double calcularSubtotal();

    /**
     * Devuelve las líneas del carrito.
     *
     * @return copia de la lista de líneas del carrito (no la referencia
     *         original, para evitar modificaciones externas).
     */
    List<LineaCarrito> getLineas();

    /**
     * Vacía completamente el carrito, eliminando todas sus líneas.
     */
    void limpiar();

    /**
     * Indica si el carrito está vacío.
     *
     * @return {@code true} si no tiene items, {@code false} en caso contrario.
     */
    boolean estaVacio();


    /**
     * Convierte el contenido del carrito en una venta completada.
     *
     * <p>Genera una nueva {@link Venta} con las líneas actuales, marca el
     * carrito como completado y lo deja vacío para una siguiente compra.</p>
     *
     * @param codigoFactura código único de factura asignado a la venta.
     * @return la venta generada a partir del contenido del carrito.
     * @throws IllegalStateException si el carrito está vacío.
     */
    Venta checkout(String codigoFactura);















}
