package com.tiendaropa.venta.servicio;


import com.tiendaropa.venta.modelo.LineaCarrito;
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

    //Retorna cantidad de items en carrito.
    //@return int: número de líneas
    int obtenerCantidadItems();

    List<LineaCarrito> getLineas();   //* Retorna copia de las líneas en carrito.
                                    // (Copia, no referencia original, para seguridad)

      void limpiar(); //Vacía completamente el carrito.
                     //Se llama después de compra exitosa.

    boolean estaVacio(); //Verifica si el carrito está vacío.
                        //@return boolean: true si no hay items















}
