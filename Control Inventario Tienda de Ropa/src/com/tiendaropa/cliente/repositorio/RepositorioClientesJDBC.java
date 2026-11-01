package com.tiendaropa.cliente.repositorio;

import com.tiendaropa.cliente.modelo.Cliente;
import com.tiendaropa.infraestructura.ConexionMySQL;
import com.tiendaropa.infraestructura.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación JDBC de {@link IGestionClientes} sobre la tabla {@code Cliente}.
 *
 * <p>Cada operación abre su propia conexión y la cierra con try-with-resources.</p>
 */
public class RepositorioClientesJDBC implements IGestionClientes {

    private static final String SQL_SELECT_BY_CEDULA =
            "SELECT cedula, nombre_completo FROM Cliente WHERE cedula = ?";

    /**
     * Busca un cliente por su cédula usando una consulta parametrizada.
     *
     * @param cedula cédula del cliente.
     * @return el cliente encontrado, o {@code null} si no existe o la cédula es nula.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public Cliente buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return null;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CEDULA)) {
            ps.setString(1, cedula.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getString("cedula"), rs.getString("nombre_completo"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar cliente por cedula", e);
        }
    }
}
