
package com.tiendaropa.infraestructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/tienda_ropa";

    private static final String USUARIO = "tienda_app";

    private ConexionMySQL() {
        // Evita crear instancias de esta clase.
    }

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
