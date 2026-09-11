package com.tiendaropa.venta.repositorio;

import com.tiendaropa.catalogo.repositorio.BaseRepositorio;
import com.tiendaropa.venta.modelo.Venta;

import java.util.List;

/**
 * Implementación en memoria de {@link IGestionVentas} para el historial
 * de ventas de la tienda de ropa de segunda mano.
 *
 * <p>Extiende {@link BaseRepositorio} para reutilizar las operaciones CRUD
 * genéricas, indexando cada venta por su código de factura.</p>
 */
public class RepositorioVentas extends BaseRepositorio<Venta> implements IGestionVentas {

    /**
     * Constructor por defecto que inicializa el almacenamiento en memoria vacío.
     */
    public RepositorioVentas() {
        super();
    }

    /**
     * Devuelve la clave única que identifica una venta dentro del repositorio.
     *
     * @param venta venta de la que se extrae su clave.
     * @return código de factura de la venta.
     */
    @Override
    protected String claveDe(Venta venta) {
        return venta.getCodigoFactura();
    }

    /**
     * Registra una nueva venta en el historial.
     *
     * @param venta venta que se desea registrar.
     * @return {@code true} si la venta se registró correctamente,
     *         {@code false} si es nula o ya existe una con el mismo
     *         código de factura.
     */
    @Override
    public boolean registrar(Venta venta) {
        return super.registrar(venta);
    }

    /**
     * Busca una venta por su código de factura.
     *
     * @param codigoFactura código de factura de la venta a localizar.
     * @return la venta encontrada o {@code null} si no existe.
     */
    @Override
    public Venta buscarPorFactura(String codigoFactura) {
        return super.buscar(codigoFactura);
    }

    /**
     * Devuelve todas las ventas registradas ordenadas por código de factura.
     *
     * @return lista no modificable con todas las ventas del historial.
     */
    @Override
    public List<Venta> obtenerTodas() {
        return super.obtenerTodos();
    }
}