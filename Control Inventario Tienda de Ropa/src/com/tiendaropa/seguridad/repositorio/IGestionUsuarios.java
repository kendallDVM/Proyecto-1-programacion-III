package com.tiendaropa.seguridad.repositorio;

import com.tiendaropa.seguridad.modelo.Usuario;

/**
 * Contrato para consultar las cuentas de acceso a la aplicación.
 *
 * <p>Solo incluye lectura: la cuenta de MySQL de la aplicación tiene permiso
 * {@code SELECT} sobre la tabla {@code Usuario}, no de modificación.</p>
 */
public interface IGestionUsuarios {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario.
     * @return el usuario encontrado, o {@code null} si no existe.
     * @throws com.tiendaropa.infraestructura.PersistenciaException si ocurre
     *         un error en la base de datos.
     */
    Usuario buscarPorUsername(String username);
}
