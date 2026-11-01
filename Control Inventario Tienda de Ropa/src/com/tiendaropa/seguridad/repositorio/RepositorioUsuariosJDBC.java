package com.tiendaropa.seguridad.repositorio;

import com.tiendaropa.infraestructura.ConexionMySQL;
import com.tiendaropa.infraestructura.PersistenciaException;
import com.tiendaropa.seguridad.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación JDBC de {@link IGestionUsuarios} sobre la tabla {@code Usuario}.
 *
 * <p>Cada operación abre su propia conexión y la cierra con try-with-resources.</p>
 */
public class RepositorioUsuariosJDBC implements IGestionUsuarios {

    private static final String SQL_SELECT_BY_USERNAME =
            "SELECT username, password_hash, rol FROM Usuario WHERE username = ?";

    /**
     * Busca un usuario por su nombre usando una consulta parametrizada.
     *
     * @param username nombre de usuario.
     * @return el usuario encontrado, o {@code null} si no existe o el nombre está vacío.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public Usuario buscarPorUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("rol"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar usuario", e);
        }
    }
}
