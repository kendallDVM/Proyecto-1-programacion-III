package com.tiendaropa.catalogo.repositorio;

import com.tiendaropa.catalogo.modelo.EstadoPrenda;
import com.tiendaropa.catalogo.modelo.Prenda;
import com.tiendaropa.catalogo.modelo.Talla;
import com.tiendaropa.catalogo.modelo.TipoPrenda;
import com.tiendaropa.infraestructura.ConexionMySQL;
import com.tiendaropa.infraestructura.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link IGestionPrendas} que persiste las prendas
 * en la base de datos MySQL {@code tienda_ropa}.
 *
 * <p>Cada operación abre su propia conexión, la utiliza y la cierra correctamente
 * mediante try-with-resources. No se mantiene una conexión global abierta.</p>
 */
public class RepositorioPrendasJDBC implements IGestionPrendas {

    private static final String SQL_INSERT =
            "INSERT INTO Prenda (codigo, tipo, talla, estado, precio, disponible) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE Prenda SET tipo = ?, talla = ?, estado = ?, precio = ?, disponible = ? WHERE codigo = ?";
    private static final String SQL_DELETE = "DELETE FROM Prenda WHERE codigo = ?";
    private static final String SQL_SELECT_BY_CODIGO =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE codigo = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda ORDER BY codigo";
    private static final String SQL_SELECT_DISPONIBLES =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE disponible = TRUE ORDER BY codigo";
    private static final String SQL_FILTER_TIPO =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE tipo = ? ORDER BY codigo";
    private static final String SQL_FILTER_TALLA =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE talla = ? ORDER BY codigo";
    private static final String SQL_FILTER_ESTADO =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE estado = ? ORDER BY codigo";
    private static final String SQL_FILTER_PRECIO =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda WHERE precio BETWEEN ? AND ? ORDER BY codigo";
    private static final String SQL_BUSCAR_TEXTO =
            "SELECT codigo, tipo, talla, estado, precio, disponible FROM Prenda "
            + "WHERE codigo LIKE ? OR tipo LIKE ? OR talla LIKE ? OR estado LIKE ? "
            + "OR CAST(precio AS CHAR) LIKE ? ORDER BY codigo";

    @Override
    public boolean registrar(Prenda prenda) {
        if (prenda == null || prenda.getCodigo() == null) {
            return false;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, prenda.getCodigo());
            ps.setString(2, prenda.getTipo().name());
            ps.setString(3, prenda.getTalla().name());
            ps.setString(4, prenda.getEstado().name());
            ps.setDouble(5, prenda.getPrecio());
            ps.setBoolean(6, prenda.isDisponible());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar prenda", e);
        }
    }

    @Override
    public boolean actualizar(Prenda prenda) {
        if (prenda == null || prenda.getCodigo() == null) {
            return false;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, prenda.getTipo().name());
            ps.setString(2, prenda.getTalla().name());
            ps.setString(3, prenda.getEstado().name());
            ps.setDouble(4, prenda.getPrecio());
            ps.setBoolean(5, prenda.isDisponible());
            ps.setString(6, prenda.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar prenda", e);
        }
    }

    @Override
    public boolean eliminar(String codigo) {
        if (codigo == null) {
            return false;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar prenda", e);
        }
    }

    @Override
    public Prenda buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CODIGO)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrenda(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar prenda por codigo", e);
        }
    }

    @Override
    public List<Prenda> obtenerTodas() {
        return consultar(SQL_SELECT_ALL);
    }

    /**
     * Devuelve solo las prendas marcadas como disponibles en la base de datos.
     *
     * @return lista de prendas con {@code disponible = TRUE}.
     */
    public List<Prenda> obtenerDisponibles() {
        return consultar(SQL_SELECT_DISPONIBLES);
    }

    @Override
    public List<Prenda> filtrarPorTipo(TipoPrenda tipo) {
        return consultarConParametro(SQL_FILTER_TIPO, tipo.name());
    }

    @Override
    public List<Prenda> filtrarPorTalla(Talla talla) {
        return consultarConParametro(SQL_FILTER_TALLA, talla.name());
    }

    @Override
    public List<Prenda> filtrarPorEstado(EstadoPrenda estado) {
        return consultarConParametro(SQL_FILTER_ESTADO, estado.name());
    }

    @Override
    public List<Prenda> filtrarPorPrecio(double precioMinimo, double precioMaximo) {
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_FILTER_PRECIO)) {
            ps.setDouble(1, precioMinimo);
            ps.setDouble(2, precioMaximo);
            try (ResultSet rs = ps.executeQuery()) {
                return mapearLista(rs);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al filtrar prendas por precio", e);
        }
    }

    @Override
    public List<Prenda> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return obtenerTodas();
        }
        String patron = "%" + texto.toLowerCase() + "%";
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_TEXTO)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, patron);
            try (ResultSet rs = ps.executeQuery()) {
                return mapearLista(rs);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar prendas por texto", e);
        }
    }

    // ---------- Métodos privados de apoyo ----------

    private List<Prenda> consultar(String sql) {
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar prendas", e);
        }
    }

    private List<Prenda> consultarConParametro(String sql, String valor) {
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return mapearLista(rs);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al filtrar prendas", e);
        }
    }

    private List<Prenda> mapearLista(ResultSet rs) throws SQLException {
        List<Prenda> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapearPrenda(rs));
        }
        return lista;
    }

    private Prenda mapearPrenda(ResultSet rs) throws SQLException {
        String codigo = rs.getString("codigo");
        TipoPrenda tipo = TipoPrenda.valueOf(rs.getString("tipo"));
        Talla talla = Talla.valueOf(rs.getString("talla"));
        EstadoPrenda estado = EstadoPrenda.valueOf(rs.getString("estado"));
        double precio = rs.getDouble("precio");
        boolean disponible = rs.getBoolean("disponible");
        return new Prenda(codigo, tipo, talla, estado, precio, disponible);
    }
}
