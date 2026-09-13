package com.tiendaropa.venta.servicio;


import com.tiendaropa.catalogo.modelo.Prenda;
import java.util.List;

/**
 * Interfaz que define el contrato para las operaciones de búsqueda de prendas.
 * Permite que la GUI busque prendas sin conocer detalles de cómo se buscan.
 * @author Liseth Briones
 */
public interface IBuscador {

/**
     * Obtiene todas las prendas disponibles del catálogo.
     *
     * @return {@code List<Prenda>} con todas las prendas disponibles.
     */
    List<Prenda> obtenerDisponibles();

    /**
     * Busca prendas con múltiples criterios simultáneamente.
     *
     * @param tipo Tipo de prenda (puede ser null para ignorar)
     * @param talla Talla (puede ser null para ignorar)
     * @param estado Estado de la prenda (puede ser null para ignorar)
     * @param precioMin Precio mínimo (0 para ignorar)
     * @param precioMax Precio máximo (0 para ignorar)
     * @return {@code List<Prenda>} que cumplen TODOS los criterios.
     */
    List<Prenda> buscarAvanzado(String tipo, String talla, String estado,
                                double precioMin, double precioMax);



}
