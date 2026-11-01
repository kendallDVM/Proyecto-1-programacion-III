package com.tiendaropa.venta.repositorio;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.cliente.modelo.Cliente;
import com.tiendaropa.infraestructura.ConexionMySQL;
import com.tiendaropa.infraestructura.PersistenciaException;
import com.tiendaropa.venta.modelo.LineaCarrito;
import com.tiendaropa.venta.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link IGestionVentas} que persiste ventas y sus
 * líneas en la base de datos MySQL {@code tienda_ropa}.
 *
 * <p>El registro de una venta se ejecuta dentro de una transacción JDBC que
 * incluye el registro del cliente (si es nuevo), la inserción de la factura,
 * los detalles de venta (Línea_Venta) y la actualización de disponibilidad de
 * las prendas vendidas. Si cualquiera de estas operaciones falla, la
 * transacción se revierte por completo.</p>
 */
public class RepositorioVentasJDBC implements IGestionVentas {

    private static final String SQL_INSERT_CLIENTE =
            "INSERT INTO Cliente (cedula, nombre_completo) VALUES (?, ?) "
            + "ON DUPLICATE KEY UPDATE cedula = cedula";
    private static final String SQL_INSERT_VENTA =
            "INSERT INTO Venta (codigo_factura, fecha, subtotal, iva, total, cliente_cedula) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_LINEA =
            "INSERT INTO Linea_Venta (factura_codigo, prenda_codigo, precio_congelado) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_DISPONIBLE =
            "UPDATE Prenda SET disponible = FALSE WHERE codigo = ?";
    private static final String SQL_SELECT_VENTA_BY_CODIGO =
            "SELECT v.codigo_factura, v.fecha, v.cliente_cedula, c.nombre_completo "
            + "FROM Venta v LEFT JOIN Cliente c ON c.cedula = v.cliente_cedula "
            + "WHERE v.codigo_factura = ?";
    private static final String SQL_SELECT_LINEAS_BY_FACTURA =
            "SELECT prenda_codigo, precio_congelado FROM Linea_Venta WHERE factura_codigo = ?";
    private static final String SQL_SELECT_ALL_VENTAS =
            "SELECT v.codigo_factura, v.fecha, v.cliente_cedula, c.nombre_completo "
            + "FROM Venta v LEFT JOIN Cliente c ON c.cedula = v.cliente_cedula "
            + "ORDER BY v.codigo_factura";
    private static final String SQL_SELECT_PRENDA =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE codigo = ?";

    /**
     * Registra una venta completa dentro de una única transacción.
     *
     * <p>Pasos: registra el cliente si todavía no existe, inserta la factura,
     * inserta cada línea y marca cada prenda como no disponible. Si algún paso
     * falla (por ejemplo, una prenda que ya fue vendida), se hace
     * {@code rollback} y no queda nada guardado.</p>
     *
     * @param venta venta a registrar; debe tener código de factura.
     * @return {@code true} si la venta se confirmó; {@code false} si la venta
     *         o su código son nulos.
     * @throws PersistenciaException si la base de datos rechaza la venta o no hay conexión.
     */
    @Override
    public boolean registrar(Venta venta) {
        if (venta == null || venta.getCodigoFactura() == null) {
            return false;
        }
        Connection conn = null;
        try {
            conn = ConexionMySQL.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Registrar el cliente si es nuevo (si ya existe, no se modifica)
            Cliente cliente = venta.getCliente();
            if (cliente != null) {
                try (PreparedStatement psCliente = conn.prepareStatement(SQL_INSERT_CLIENTE)) {
                    psCliente.setString(1, cliente.getCedula());
                    psCliente.setString(2, cliente.getNombreCompleto());
                    psCliente.executeUpdate();
                }
            }

            // 2. Insertar la venta (factura)
            try (PreparedStatement psVenta = conn.prepareStatement(SQL_INSERT_VENTA)) {
                psVenta.setString(1, venta.getCodigoFactura());
                psVenta.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
                psVenta.setDouble(3, venta.getSubtotal());
                psVenta.setDouble(4, venta.getIva());
                psVenta.setDouble(5, venta.getTotal());
                if (cliente != null) {
                    psVenta.setString(6, cliente.getCedula());
                } else {
                    psVenta.setNull(6, Types.VARCHAR);
                }
                psVenta.executeUpdate();
            }

            // 3. Insertar las líneas de detalle y actualizar disponibilidad de prendas
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

    /**
     * Busca una venta por su código de factura, con sus líneas y su cliente.
     *
     * @param codigoFactura código de la factura.
     * @return la venta encontrada, o {@code null} si no existe o el código es nulo.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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
                return mapearVenta(conn, rs);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar venta por factura", e);
        }
    }

    /**
     * Devuelve todas las ventas registradas, ordenadas por código de factura.
     *
     * @return lista de ventas con sus líneas y clientes; vacía si no hay ventas.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL_VENTAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapearVenta(conn, rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener todas las ventas", e);
        }
        return ventas;
    }

    // ---------- Métodos privados ----------

    /**
     * Convierte la fila actual de una consulta de ventas en un objeto {@link Venta}.
     *
     * @param conn conexión abierta, usada para cargar las líneas de la venta.
     * @param rs   resultado posicionado en la fila de la venta.
     * @return venta reconstruida con su fecha original, sus líneas y su cliente.
     * @throws SQLException si no se puede leer alguna columna.
     */
    private Venta mapearVenta(Connection conn, ResultSet rs) throws SQLException {
        String codigoFactura = rs.getString("codigo_factura");
        LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
        String cedula = rs.getString("cliente_cedula");
        Cliente cliente = cedula == null ? null : new Cliente(cedula, rs.getString("nombre_completo"));
        List<LineaCarrito> lineas = cargarLineas(conn, codigoFactura);
        return new Venta(codigoFactura, fecha, lineas, cliente);
    }

    /**
     * Carga las líneas de una factura con el precio congelado de cada prenda.
     *
     * @param conn          conexión abierta que se reutiliza para las consultas.
     * @param codigoFactura código de la factura cuyas líneas se cargan.
     * @return lista de líneas de la venta.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Busca los datos de una prenda usando una conexión ya abierta.
     *
     * @param conn         conexión abierta que se reutiliza.
     * @param codigoPrenda código de la prenda.
     * @return la prenda encontrada, o {@code null} si no existe.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    private Prenda buscarPrendaPorCodigo(Connection conn, String codigoPrenda) {
        try {
            try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_PRENDA)) {
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
