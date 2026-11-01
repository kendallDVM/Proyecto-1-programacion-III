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

    /**
     * Inserta una prenda nueva en la tabla {@code Prenda}.
     *
     * @param prenda prenda a registrar; debe tener código.
     * @return {@code true} si se insertó; {@code false} si la prenda o su código son nulos.
     * @throws PersistenciaException si MySQL rechaza la inserción (por ejemplo,
     *                               código duplicado) o no hay conexión.
     */
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

    /**
     * Actualiza tipo, talla, estado, precio y disponibilidad de una prenda existente.
     *
     * @param prenda prenda con los datos nuevos; se identifica por su código.
     * @return {@code true} si se actualizó una fila; {@code false} si no existe
     *         una prenda con ese código o la prenda es nula.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Elimina físicamente una prenda de la tabla {@code Prenda}.
     *
     * @param codigo código de la prenda a eliminar.
     * @return {@code true} si se eliminó; {@code false} si no existía o el código es nulo.
     * @throws PersistenciaException si la prenda forma parte de una venta
     *                               (clave foránea) o hay un error de conexión.
     */
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

    /**
     * Busca una prenda por su código.
     *
     * @param codigo código de la prenda.
     * @return la prenda encontrada, o {@code null} si no existe o el código es nulo.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Devuelve todas las prendas del catálogo, vendidas o no, ordenadas por código.
     *
     * @return lista de prendas; vacía si no hay ninguna.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Devuelve las prendas de un tipo determinado.
     *
     * @param tipo tipo de prenda a filtrar.
     * @return lista de prendas de ese tipo, ordenadas por código.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public List<Prenda> filtrarPorTipo(TipoPrenda tipo) {
        return consultarConParametro(SQL_FILTER_TIPO, tipo.name());
    }

    /**
     * Devuelve las prendas de una talla determinada.
     *
     * @param talla talla a filtrar.
     * @return lista de prendas de esa talla, ordenadas por código.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public List<Prenda> filtrarPorTalla(Talla talla) {
        return consultarConParametro(SQL_FILTER_TALLA, talla.name());
    }

    /**
     * Devuelve las prendas con un estado de conservación determinado.
     *
     * @param estado estado a filtrar.
     * @return lista de prendas con ese estado, ordenadas por código.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    @Override
    public List<Prenda> filtrarPorEstado(EstadoPrenda estado) {
        return consultarConParametro(SQL_FILTER_ESTADO, estado.name());
    }

    /**
     * Devuelve las prendas cuyo precio está dentro de un rango (incluye los extremos).
     *
     * @param precioMinimo precio mínimo del rango.
     * @param precioMaximo precio máximo del rango.
     * @return lista de prendas dentro del rango, ordenadas por código.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Busca prendas cuyo código, tipo, talla, estado o precio contengan un texto.
     *
     * <p>El texto se pasa como parámetro del {@code PreparedStatement}, nunca
     * concatenado en la consulta.</p>
     *
     * @param texto texto a buscar; si está vacío se devuelven todas las prendas.
     * @return lista de prendas que coinciden, ordenadas por código.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Ejecuta una consulta sin parámetros que devuelve prendas.
     *
     * @param sql consulta {@code SELECT} fija definida en esta clase.
     * @return lista de prendas obtenidas.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
    private List<Prenda> consultar(String sql) {
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearLista(rs);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar prendas", e);
        }
    }

    /**
     * Ejecuta una consulta con un único parámetro de texto que devuelve prendas.
     *
     * @param sql   consulta {@code SELECT} fija con un marcador {@code ?}.
     * @param valor valor que reemplaza al marcador.
     * @return lista de prendas obtenidas.
     * @throws PersistenciaException si ocurre un error en la base de datos.
     */
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

    /**
     * Convierte todas las filas de un resultado en objetos {@link Prenda}.
     *
     * @param rs resultado de una consulta sobre la tabla {@code Prenda}.
     * @return lista de prendas, en el mismo orden que las filas.
     * @throws SQLException si no se puede leer el resultado.
     */
    private List<Prenda> mapearLista(ResultSet rs) throws SQLException {
        List<Prenda> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapearPrenda(rs));
        }
        return lista;
    }

    /**
     * Convierte la fila actual de un resultado en una {@link Prenda}.
     *
     * <p>Los textos de tipo, talla y estado se convierten a sus enumeraciones.</p>
     *
     * @param rs resultado posicionado en la fila que se desea leer.
     * @return prenda con los datos de la fila.
     * @throws SQLException si no se puede leer alguna columna.
     */
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
