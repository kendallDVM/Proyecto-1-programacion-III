package com.tiendaropa.cliente.repositorio;

import com.tiendaropa.cliente.modelo.Cliente;

/**
 * Contrato para consultar los clientes registrados en la tienda.
 *
 * <p>El registro de un cliente nuevo se realiza junto con la venta, dentro de
 * la misma transacción, para que no quede un cliente guardado si la venta
 * falla.</p>
 */
public interface IGestionClientes {

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula del cliente.
     * @return el cliente encontrado, o {@code null} si no existe.
     * @throws com.tiendaropa.infraestructura.PersistenciaException si ocurre
     *         un error en la base de datos.
     */
    Cliente buscarPorCedula(String cedula);
}
