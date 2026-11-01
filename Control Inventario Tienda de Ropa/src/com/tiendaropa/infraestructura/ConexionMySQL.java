
package com.tiendaropa.infraestructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor de conexiones JDBC a la base de datos MySQL {@code tienda_ropa}.
 *
 * <p>Usa la cuenta de aplicación {@code tienda_app}, que solo tiene los permisos
 * necesarios sobre las tablas del sistema. La contraseña no se guarda en el
 * código: se lee de la variable de entorno {@code TIENDA_DB_PASSWORD}.</p>
 *
 * <p>No mantiene una conexión global abierta: cada llamada crea una conexión
 * nueva que el llamador debe cerrar, normalmente con try-with-resources.</p>
 */
public final class ConexionMySQL {

    /** Dirección JDBC del servidor MySQL local y de la base de datos. */
    private static final String URL =
            "jdbc:mysql://localhost:3306/tienda_ropa";

    /** Cuenta de aplicación con permisos mínimos (no se usa root). */
    private static final String USUARIO = "tienda_app";

    /**
     * Constructor privado: la clase solo ofrece métodos estáticos.
     */
    private ConexionMySQL() {
        // Evita crear instancias de esta clase.
    }

    /**
     * Abre una nueva conexión a la base de datos con la cuenta de aplicación.
     *
     * @return conexión JDBC abierta; el llamador es responsable de cerrarla.
     * @throws SQLException si la variable de entorno {@code TIENDA_DB_PASSWORD}
     *                      no está configurada o si MySQL rechaza la conexión.
     */
    public static Connection obtenerConexion() throws SQLException {
        String contrasena = System.getenv("TIENDA_DB_PASSWORD");

        if (contrasena == null || contrasena.isBlank()) {
            throw new SQLException(
                    "No se ha configurado la variable de entorno TIENDA_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USUARIO, contrasena);
    }
}
