package com.tiendaropa.venta.repositorio;

import com.tiendaropa.venta.modelo.Venta;

import java.util.List;

/**
 * Contrato que define las operaciones de gestión del historial de ventas
 * de la tienda de ropa de segunda mano.
 *
 * <p>Permite registrar una venta, consultar el historial completo y buscar
 * una venta específica por su código de factura.</p>
 */
public interface IGestionVentas {

    /**
     * Registra una nueva venta en el historial.
     *
     * @param venta venta que se desea registrar.
     * @return {@code true} si la venta se registró correctamente,
     *         {@code false} si es nula o ya existe una con el mismo
     *         código de factura.
     */
    boolean registrar(Venta venta);

    /**
     * Busca una venta por su código de factura.
     *
     * @param codigoFactura código de factura de la venta a localizar.
     * @return la venta encontrada o {@code null} si no existe.
     */
    Venta buscarPorFactura(String codigoFactura);

    /**
     * Devuelve todas las ventas registradas ordenadas por código de factura.
     *
     * @return lista no modificable con todas las ventas del historial.
     */
    List<Venta> obtenerTodas();
}