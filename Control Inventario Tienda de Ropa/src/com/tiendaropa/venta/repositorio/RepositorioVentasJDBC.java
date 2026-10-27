package com.tiendaropa.venta.repositorio;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.infraestructura.ConexionMySQL;
import com.tiendaropa.infraestructura.PersistenciaException;
import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link IGestionVentas} que persiste ventas y sus
 * líneas en la base de datos MySQL {@code tienda_ropa}.
 *
 * <p>El registro de una venta se ejecuta dentro de una transacción JDBC que
 * incluye la inserción de la factura, los detalles de venta (Línea_Venta) y
 * la actualización de disponibilidad de las prendas vendidas. Si cualquiera
 * de estas operaciones falla, la transacción se revierte por completo.</p>
 */
public class RepositorioVentasJDBC implements IGestionVentas {

    private static final String SQL_INSERT_VENTA =
            "INSERT INTO Venta (codigo_factura, fecha, subtotal, iva, total, cliente_cedula) VALUES (?, ?, ?, ?, ?, NULL)";
    private static final String SQL_INSERT_LINEA =
            "INSERT INTO Linea_Venta (factura_codigo, prenda_codigo, precio_congelado) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_DISPONIBLE =
            "UPDATE Prenda SET disponible = FALSE WHERE codigo = ?";
    private static final String SQL_SELECT_VENTA_BY_CODIGO =
            "SELECT codigo_factura, fecha, subtotal, iva, total FROM Venta WHERE codigo_factura = ?";
    private static final String SQL_SELECT_LINEAS_BY_FACTURA =
            "SELECT prenda_codigo, precio_congelado FROM Linea_Venta WHERE factura_codigo = ?";
    private static final String SQL_SELECT_ALL_VENTAS =
            "SELECT codigo_factura, fecha, subtotal, iva, total FROM Venta ORDER BY codigo_factura";

    @Override
    public boolean registrar(Venta venta) {
        if (venta == null || venta.getCodigoFactura() == null) {
            return false;
        }
        Connection conn = null;
        try {
            conn = ConexionMySQL.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Insertar la venta (factura)
            try (PreparedStatement psVenta = conn.prepareStatement(SQL_INSERT_VENTA)) {
                psVenta.setString(1, venta.getCodigoFactura());
                psVenta.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
                psVenta.setDouble(3, venta.getSubtotal());
                psVenta.setDouble(4, venta.getIva());
                psVenta.setDouble(5, venta.getTotal());
                psVenta.executeUpdate();
            }

            // 2. Insertar las líneas de detalle y actualizar disponibilidad de prendas
            try (PreparedStatement psLinea = conn.prepareStatement(SQL_INSERT_LINEA);
                 PreparedStatement psDisponible = conn.prepareStatement(SQL_UPDATE_DISPONIBLE)) {
                for (LineaCarrito linea : venta.getLineas()) {
                    String codigoPrenda = linea.getPrenda().getCodigo();
                    psLinea.setString(1, venta.getCodigoFactura());
                    psLinea.setString(2, codigoPrenda);
                    psLinea.setDouble(3, linea.getPrecioEnMomento());
                    psLinea.addBatch();

                    psDisponible.setString(1, codigoPrenda);
                    psDisponible.addBatch();
                }
                psLinea.executeBatch();
                psDisponible.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Ignorar errores de rollback; el error original es el relevante
                }
            }
            throw new PersistenciaException("Error al registrar venta", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // No se propaga; la conexión ya no es usable
                }
            }
        }
    }

    @Override
    public Venta buscarPorFactura(String codigoFactura) {
        if (codigoFactura == null) {
            return null;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_VENTA_BY_CODIGO)) {
            ps.setString(1, codigoFactura);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
                List<LineaCarrito> lineas = cargarLineas(conn, codigoFactura);
                return new Venta(codigoFactura, fecha, lineas);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar venta por factura", e);
        }
    }

    @Override
    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL_VENTAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String codigoFactura = rs.getString("codigo_factura");
                LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
                List<LineaCarrito> lineas = cargarLineas(conn, codigoFactura);
                ventas.add(new Venta(codigoFactura, fecha, lineas));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener todas las ventas", e);
        }
        return ventas;
    }

    // ---------- Métodos privados ----------

    private List<LineaCarrito> cargarLineas(Connection conn, String codigoFactura) {
        List<LineaCarrito> lineas = new ArrayList<>();
        try {
            try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_LINEAS_BY_FACTURA)) {
                ps.setString(1, codigoFactura);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String codigoPrenda = rs.getString("prenda_codigo");
                        double precioCongelado = rs.getDouble("precio_congelado");
                        Prenda prenda = buscarPrendaPorCodigo(conn, codigoPrenda);
                        if (prenda != null) {
                            lineas.add(new LineaCarrito(prenda, precioCongelado));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar lineas de venta", e);
        }
        return lineas;
    }

    private Prenda buscarPrendaPorCodigo(Connection conn, String codigoPrenda) {
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE codigo = ?")) {
                ps.setString(1, codigoPrenda);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String codigo = rs.getString("codigo");
                        TipoPrenda tipo = TipoPrenda.valueOf(rs.getString("tipo"));
                        Talla talla = Talla.valueOf(rs.getString("talla"));
                        EstadoPrenda estado = EstadoPrenda.valueOf(rs.getString("estado"));
                        double precio = rs.getDouble("precio");
                        boolean disponible = rs.getBoolean("disponible");
                        return new Prenda(codigo, tipo, talla, estado, precio, disponible);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar prenda por codigo", e);
        }
    }
}
